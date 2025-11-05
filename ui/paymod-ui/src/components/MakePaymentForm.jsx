import { useEffect, useState } from "react";
import axios from "axios";
import { StackLayout, Text, FormField, Input, Button, StatusIndicator } from "@salt-ds/core";

export default function MakePaymentForm({ onClose, onSuccess }) {
    const [users, setUsers] = useState([]);
    const [selectedUserId, setSelectedUserId] = useState("");
    const [amount, setAmount] = useState("");
    const [currency] = useState("USD");
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState(null);

    const cents = Math.round(Number(amount) * 100);

    useEffect(() => {
        (async () => {
            try {
                const r = await axios.get("/api/users");
                const list = r.data ?? [];
                setUsers(list);
                if (list.length && !selectedUserId) setSelectedUserId(String(list[0].id));
            } catch {}
        })();
    }, []);

    async function handleSubmit(e) {
        e.preventDefault();
        const amt = Number(amount);
        if (!selectedUserId || !amt || Number.isNaN(amt) || amt <= 0) {
            setMsg({ type: "error", text: "Choose a user and enter a valid amount." });
            return;
        }
        setBusy(true); setMsg(null);
        try {
            await axios.post(
                "/api/payments",
                { amount: cents, currency },
                { headers: { "X-User-ID": selectedUserId } }
            );
            setMsg({ type: "success", text: "Payment created." });
            onSuccess?.();
            onClose?.();
        } catch {
            setMsg({ type: "error", text: "Failed to create payment." });
        } finally { setBusy(false); }
    }

    return (
        <form onSubmit={handleSubmit}>
            <StackLayout gap={2}>
                <Text styleAs="h3">Make a Payment</Text>

                <FormField label="User">
                    <select
                        value={selectedUserId}
                        onChange={(e) => setSelectedUserId(e.target.value)}
                        disabled={busy || users.length === 0}
                        style={{ padding: 8, minWidth: 220 }}
                    >
                        {users.length === 0 && <option value="">No users yet</option>}
                        {users.map(u => <option key={u.id} value={u.id}>{u.email} (#{u.id})</option>)}
                    </select>
                </FormField>

                <FormField label="Amount (USD)">
                    <Input
                        value={amount}
                        inputMode="decimal"
                        placeholder="19.99"
                        onChange={(e) => setAmount(e.target.value)}
                        disabled={busy}
                    />
                </FormField>

                <FormField label="Currency">
                    <Input value={currency} disabled />
                </FormField>

                <StackLayout direction="row" gap={1}>
                    <Button onClick={onClose} variant="secondary" disabled={busy}>Cancel</Button>
                    <Button type="submit" disabled={busy || !selectedUserId || !amount}>Create Payment</Button>
                </StackLayout>

                {msg && <StatusIndicator status={msg.type === "success" ? "success" : "error"}>{msg.text}</StatusIndicator>}
            </StackLayout>
        </form>
    );
}
