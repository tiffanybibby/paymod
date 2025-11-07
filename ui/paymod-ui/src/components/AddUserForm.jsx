import { useState } from "react";
import axios from "axios";
import { StackLayout, Text, FormField, Input, Button, StatusIndicator } from "@salt-ds/core";

export default function AddUserForm({ onClose, onSuccess }) {
    const [email, setEmail] = useState("");
    const [firstName, setFirst] = useState("");
    const [lastName, setLast] = useState("");
    const [billingPostalCode, setPostal] = useState("");
    const [billingCountry, setCountry] = useState("US");
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState(null);

    function normalizeCountry(value) {
        return (value || "").toUpperCase().slice(0, 2);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setMsg(null);
        if (!email.trim()) {
            setMsg({ type: "error", text: "Email is required." });
            return;
        }
        const payload = {
            email: email.trim(),
            firstName: firstName?.trim() || null,
            lastName: lastName?.trim() || null,
            billingPostalCode: billingPostalCode?.trim() || null,
            billingCountry: normalizeCountry(billingCountry) || "US",
        };
        setBusy(true);
        try {
            await axios.post("/api/users", payload);
            setMsg({ type: "success", text: "User created." });
            console.log(msg)
            onSuccess?.();
            onClose?.();
        } catch (err) {
            setMsg({ type: "error", text: "Failed to create user. (Email may already exist.)" });
        } finally {
            setBusy(false);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <StackLayout gap={2}>
                <Text styleAs="h3">Add User</Text>
                <FormField label="Email">
                    <Input
                        type="email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="you@example.com"
                        disabled={busy}
                    />
                </FormField>
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                    <FormField label="First name">
                        <Input value={firstName} placeholder="First Name" onChange={(e) => setFirst(e.target.value)} disabled={busy} />
                    </FormField>
                    <FormField label="Last name">
                        <Input value={lastName} placeholder="Last Name" onChange={(e) => setLast(e.target.value)} disabled={busy} />
                    </FormField>
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: 8 }}>
                    <FormField label="Billing postal code">
                        <Input
                            value={billingPostalCode}
                            maxLength={20}
                            onChange={(e) => setPostal(e.target.value)}
                            placeholder="07102"
                            disabled={busy}
                        />
                    </FormField>
                    <FormField label="Country">
                        <Input
                            value={billingCountry}
                            maxLength={2}
                            onChange={(e) => setCountry(e.target.value)}
                            placeholder="US"
                            disabled={true}
                        />
                    </FormField>
                </div>
                <StackLayout direction="row" gap={1}>
                    <Button variant="secondary" onClick={onClose} disabled={busy}>Cancel</Button>
                    <Button type="submit" disabled={busy || !email.trim()}>Create</Button>
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
