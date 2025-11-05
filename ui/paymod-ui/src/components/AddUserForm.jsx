import { useState } from "react";
import axios from "axios";
import { StackLayout, Text, FormField, Input, Button, StatusIndicator } from "@salt-ds/core";

export default function AddUserForm({ onClose, onSuccess }) {
    const [email, setEmail] = useState("");
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState(null);

    async function handleSubmit(e) {
        e.preventDefault();
        if (!email.trim()) return;
        setBusy(true); setMsg(null);
        try {
            await axios.post("/api/users", { email: email.trim() });
            setMsg({ type: "success", text: "User added." });
            onSuccess?.();
            onClose?.();
        } catch {
            setMsg({ type: "error", text: "Failed to add user." });
        } finally { setBusy(false); }
    }

    return (
        <form onSubmit={handleSubmit}>
            <StackLayout gap={2}>
                <Text styleAs="h3">Add User</Text>
                <FormField label="Email">
                    <Input
                        autoFocus
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="user@example.com"
                        disabled={busy}
                    />
                </FormField>
                <StackLayout direction="row" gap={1}>
                    <Button onClick={onClose} variant="secondary" disabled={busy}>Cancel</Button>
                    <Button type="submit" disabled={busy || !email.trim()}>Add User</Button>
                </StackLayout>
                {msg && <StatusIndicator status={msg.type === "success" ? "success" : "error"}>{msg.text}</StatusIndicator>}
            </StackLayout>
        </form>
    );
}
