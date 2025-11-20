import { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import {StackLayout, Text, FormField, Input, Button, Panel, StatusIndicator, Label} from "@salt-ds/core";
import { useAuth } from "../context/AuthContext.jsx";

export default function CustomerCheckout() {
    const {authHeaders} = useAuth();
    const [amount, setAmount] = useState("");
    const [methods, setMethods] = useState([]);
    const [pmId, setPmId] = useState("");
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState(null);
    const [receipt, setReceipt] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const r = await axios.get("/api/wallet/payment-methods", {
                    headers: authHeaders,
                });
                const list = r.data ?? [];
                setMethods(list);
                const def =
                    list.find((m) => m.isDefault || m.default) || list[0];
                if (def?.id) setPmId(String(def.id));
            } catch (e) {
                console.error(e);
                setMsg({
                    type: "error",
                    text: "Unable to load payment methods.",
                });
            }
        })();
    }, [authHeaders]);

    async function handleSubmit(e) {
        e.preventDefault();
        setMsg(null);
        setReceipt(null);
        const amt = Number(amount);
        if (!pmId) {
            setMsg({
                type: "error",
                text: "Select a payment method (or add one in Wallet).",
            });
            return;
        }
        if (!amt || Number.isNaN(amt) || amt <= 0) {
            setMsg({
                type: "error",
                text: "Enter a valid amount (e.g., 19.99).",
            });
            return;
        }
        setBusy(true);
        try {
            const body = {
                paymentMethodId: Number(pmId),
                amount: Number(amt.toFixed(2)),
                currency: "USD",
            };

            const {data} = await axios.post("/api/payments", body, {
                headers: authHeaders,
            });
            if (!data || !data.id) {
                setMsg({
                    type: "error",
                    text: "Payment created but response was incomplete.",
                });
                return;
            }
            setReceipt(data);
            setMsg({
                type: "success",
                text:
                    data.status === "SUCCESS" ? "Payment succeeded." : data.status === "FAILED" ? "Payment failed." : "Payment submitted.",
            });
        } catch (e) {
            console.error(e);
            setMsg({
                type: "error",
                text: "Couldn’t start the payment. Please try again.",
            });
        } finally {
            setBusy(false);
        }
    }

    const fmtAmount = (v) =>
        typeof v === "number" ? `$${v.toFixed(2)}` : v ? `$${Number(v).toFixed(2)}` : "$0.00";

    return (
        <div className="container page">
            <div className="page-narrow">
                <Panel style={{padding: 16}}>
                    <form onSubmit={handleSubmit}>
                        <StackLayout gap={2}>
                            <Text styleAs="h2">Checkout</Text>
                            <FormField label="Payment method">
                                {methods.length === 0 ? (
                                    <div
                                        style={{
                                            display: "flex",
                                            alignItems: "center",
                                            gap: 8,
                                        }}>
                                        <Text>No payment methods</Text>
                                        <Link to="/wallet">Go to Wallet</Link>
                                    </div>
                                ) : (
                                    <select
                                        value={pmId}
                                        onChange={(e) => setPmId(e.target.value)}
                                        disabled={busy}
                                        style={{padding: 8, minWidth: 260}}
                                        required>
                                        {methods.map((m) => (
                                            <option key={m.id} value={m.id}>
                                                {(m.label || m.brand)} ••••{" "}
                                                {m.last4} ({m.expMonth}/
                                                {m.expYear}){" "}
                                                {m.isDefault || m.default ? "— default" : ""}
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
                            <Button
                                type="submit"
                                disabled={
                                    busy ||
                                    !amount ||
                                    !pmId ||
                                    methods.length === 0
                                }>
                                {busy ? "Processing..." : "Pay"}
                            </Button>
                            {msg && (
                                <StackLayout direction="row" align="center" gap={1} style={{ marginTop: 4 }}>
                                    <StatusIndicator status={msg.type === "success" ? "success" : "error"}/>
                                    <Label> {msg.text} </Label>
                                </StackLayout>
                            )}
                            {receipt && (
                                <Panel
                                    style={{
                                        marginTop: 8,
                                        padding: 12,
                                        borderRadius: 8,
                                    }}>
                                    <StackLayout gap={1}>
                                        <Text styleAs="h3">Payment receipt</Text>
                                        <Text
                                            style={{
                                                fontSize: 12,
                                                opacity: 0.8,
                                            }}>
                                            Payment ID: {receipt.id}
                                        </Text>
                                        <Text>
                                            Amount:{" "}
                                            <strong>
                                                {fmtAmount(receipt.amount)}{" "}
                                                {receipt.currency || "USD"}
                                            </strong>
                                        </Text>
                                        <StatusIndicator
                                            status={receipt.status === "SUCCESS" ? "success" : receipt.status === "FAILED" ? "error" : "info"}>
                                            {receipt.status === "SUCCESS" && "Payment succeeded"}
                                            {receipt.status === "FAILED" && "Payment failed"}
                                            {receipt.status === "PENDING" && "Payment processing"}
                                        </StatusIndicator>
                                        {receipt.createdAt && (
                                            <Text
                                                style={{
                                                    fontSize: 12,
                                                    opacity: 0.8,
                                                }}>
                                                Created at:{" "}
                                                {new Date(receipt.createdAt).toLocaleString()}
                                            </Text>
                                        )}
                                        <Link to={`/receipt/${receipt.id}`}>
                                            View full receipt
                                        </Link>
                                    </StackLayout>
                                </Panel>
                            )}
                        </StackLayout>
                    </form>
                </Panel>
            </div>
        </div>
    );
}
