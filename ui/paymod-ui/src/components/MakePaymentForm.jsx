import { useEffect, useState } from "react";
import axios from "axios";
import { StackLayout, Text, FormField, Input, Button, StatusIndicator } from "@salt-ds/core";
import { useAuth } from "../context/AuthContext.jsx";
import { Link } from "react-router-dom";

export default function MakePaymentForm({ onClose, onSuccess }) {
    const { authHeaders } = useAuth();
    const [amount, setAmount] = useState("");
    const [currency] = useState("USD");
    const [methods, setMethods] = useState([]);
    const [pmId, setPmId] = useState("");
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const r = await axios.get("/api/wallet/payment-methods", { headers: authHeaders });
                const list = r.data ?? [];
                setMethods(list);
                const def = list.find(m => m.default);
                if (def) setPmId(String(def.id));
                else if (list[0]?.id) setPmId(String(list[0].id));
            } catch {}
        })();
    }, []);

    async function handleSubmit(e) {
        e.preventDefault();
        setMsg(null);
        const amt = Number(amount);
        if (!pmId) {
            setMsg({ type: "error", text: "Select a payment method (or add one in Wallet)." });
            return;
        }
        if (!amt || Number.isNaN(amt) || amt <= 0) {
            setMsg({ type: "error", text: "Enter a valid amount (e.g., 19.99)." });
            return;
        }
        setBusy(true);
        try {
            await axios.post(
                "/api/payments",
                { amount: Number(amt.toFixed(2)), currency, paymentMethodId: Number(pmId) },
                { headers: authHeaders }
            );
            setMsg({ type: "success", text: "Payment created." });
            onSuccess?.();
            onClose?.();
        } catch (err) {
            setMsg({ type: "error", text: "Failed to create payment." });
        } finally {
            setBusy(false);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <StackLayout gap={2}>
                <Text styleAs="h3">Make a Payment</Text>
                <FormField label="Payment method">
                    {methods.length === 0 ? (
                        <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                            <Text>No payment methods found.</Text>
                            <Link to="/wallet">Go to Wallet</Link>
                        </div>
                    ) : (
                        <select
                            value={pmId}
                            onChange={(e) => setPmId(e.target.value)}
                            disabled={busy}
                            style={{ padding: 8, minWidth: 260 }}
                            required>
                            {methods.map(m => (
                                <option key={m.id} value={m.id}>
                                    {(m.label || m.brand)} •••• {m.last4} ({m.expMonth}/{m.expYear}) {m.default ? "— default" : ""}
                                </option>
                            ))}
                        </select>
                    )}
                </FormField>
                <FormField label="Amount (USD)">
                    <Input
                        value={amount}
                        inputMode="decimal"
                        placeholder="19.99"
                        onChange={(e) => setAmount(e.target.value)}
                        disabled={busy || methods.length === 0}
                    />
                </FormField>
                <FormField label="Currency">
                    <Input value={currency} disabled />
                </FormField>
                <StackLayout direction="row" gap={1}>
                    <Button onClick={onClose} variant="secondary" disabled={busy}>Cancel</Button>
                    <Button type="submit" disabled={busy || !amount || !pmId || methods.length === 0}>
                        Create Payment
                    </Button>
                </StackLayout>
                {msg && (
                    <StatusIndicator status={msg.type === "success" ? "success" : "error"}>
                        {msg.text}
                    </StatusIndicator>
                )}
            </StackLayout>
        </form>
    );
}
