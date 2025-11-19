import { useState } from "react";
import axios from "axios";
import { Button, FormField, Input, Panel, StackLayout, Text } from "@salt-ds/core";
import { useAuth } from "../context/AuthContext.jsx";
import { useNavigate } from "react-router-dom";

export default function Login() {
    const { login } = useAuth();
    const nav = useNavigate();
    const [email, setEmail] = useState("");
    const [firstName, setFirst] = useState("");
    const [lastName, setLast] = useState("");
    const [busy, setBusy] = useState(false);

    async function submit(e) {
        e.preventDefault();
        setBusy(true);
        try {
            const r = await axios.post("/api/auth/login", { email, firstName, lastName });
            login(r.data);
            nav("/checkout");
        } finally { setBusy(false); }
    }

    return (
        <div className="container page">
            <div className="page-narrow">
            <Panel style={{ padding: 16 }}>
                <form onSubmit={submit}>
                    <StackLayout gap={2}>
                        <Text styleAs="h2">Sign in</Text>
                        <FormField label="email"><Input placeholder="user@example.com" type="email" required value={email} onChange={e=>setEmail(e.target.value)} /></FormField>
                        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                            <FormField label="firstName"><Input placeholder="First name" value={firstName} onChange={e=>setFirst(e.target.value)} /></FormField>
                            <FormField label="LastName"><Input placeholder="Last name" value={lastName} onChange={e=>setLast(e.target.value)} /></FormField>
                        </div>
                        <Button type="submit" disabled={busy}>{busy ? "Signing in…" : "Sign in"}</Button>
                        <Text style={{ fontSize: 12, opacity: .7 }}>Demo-only: creates the user if not found.</Text>
                    </StackLayout>
                </form>
            </Panel>
        </div>
        </div>
    );
}
