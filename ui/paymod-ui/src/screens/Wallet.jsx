import { useEffect, useState } from "react";
import axios from "axios";
import { Button, FormField, Input, Panel, StackLayout, Text } from "@salt-ds/core";
import { useAuth } from "../context/AuthContext.jsx";

export default function Wallet() {
    const { authHeaders } = useAuth();
    const [methods, setMethods] = useState([]);
    const [loading, setLoading] = useState(true);
    const [brand, setBrand] = useState("");
    const [last4, setLast4] = useState("");
    const [expMonth, setExpMonth] = useState("");
    const [expYear, setExpYear] = useState("");
    const [label, setLabel] = useState("");

    async function load() {
        setLoading(true);
        try {
            const r = await axios.get("/api/wallet/payment-methods", { headers: authHeaders });
            setMethods(r.data ?? []);
        } finally {
            setLoading(false);
        }
    }
    useEffect(() => { load(); }, []);

    async function add(e) {
        e.preventDefault();

        if (!brand.trim()) return alert("Brand is required");
        if (!/^\d{4}$/.test(last4)) return alert("Last4 must be 4 digits");
        const mm = Number(expMonth), yy = Number(expYear);
        if (!mm || mm < 1 || mm > 12) return alert("Exp month must be 1–12");
        if (!yy || yy < 2024) return alert("Exp year must be >= current year");

        const body = {
            brand: brand.trim(),
            last4: last4.trim(),
            expMonth: mm,
            expYear: yy,
            label: label.trim() || null,
        };

        try {
            await axios.post("/api/wallet/payment-methods", body, { headers: authHeaders });
            setBrand(""); setLast4(""); setExpMonth(""); setExpYear(""); setLabel("");
            await load();
        } catch (err) {
            alert(err?.response?.data || "Failed to add payment method");
        }
    }

    async function makeDefault(id) {
        await axios.patch(`/api/wallet/payment-methods/${id}`, { isDefault: true }, { headers: authHeaders });
        await load();
    }
    async function remove(id) {
        if (!confirm("Delete this payment method?")) return;
        await axios.delete(`/api/wallet/payment-methods/${id}`, { headers: authHeaders });
        await load();
    }

    return (
        <div className="container page">
            <StackLayout gap={2}>
                <Text styleAs="h2">Wallet</Text>

                <Panel style={{ padding: 12 }}>
                    <form onSubmit={add} style={{ display: "grid", gap: 8, gridTemplateColumns: "1fr 1fr 1fr 1fr 2fr" }}>
                        <FormField label="Brand">
                            <Input placeholder="VISA/MC/AMEX" value={brand} onChange={e => setBrand(e.target.value)} />
                        </FormField>
                        <FormField label="Last4">
                            <Input placeholder="1234" value={last4} maxLength={4} onChange={e => setLast4(e.target.value)} />
                        </FormField>
                        <FormField label="MM">
                            <Input placeholder="MM" value={expMonth} onChange={e => setExpMonth(e.target.value)} />
                        </FormField>
                        <FormField label="YYYY">
                            <Input placeholder="YYYY" value={expYear} onChange={e => setExpYear(e.target.value)} />
                        </FormField>
                        <FormField label="Label">
                            <Input placeholder="Personal" value={label} onChange={e => setLabel(e.target.value)} />
                        </FormField>
                        <div style={{ gridColumn: "1 / -1" }}>
                            <Button type="submit">Add Payment Method</Button>
                        </div>
                    </form>
                </Panel>
                <Panel style={{ padding: 0 }}>
                    {loading ? (
                        <div style={{ padding: 12 }}>Loading…</div>
                    ) : methods.length === 0 ? (
                        <div style={{ padding: 12 }}>No payment methods yet.</div>
                    ) : (
                        <table style={{ width: "100%" }}>
                            <thead>
                            <tr style={{ background: "rgba(255,255,255,0.06)" }}>
                                <th style={{ textAlign: "left", padding: 8 }}>Label</th>
                                <th style={{ textAlign: "left", padding: 8 }}>Brand</th>
                                <th style={{ textAlign: "left", padding: 8 }}>Last4</th>
                                <th style={{ textAlign: "left", padding: 8 }}>Exp</th>
                                <th style={{ textAlign: "left", padding: 8 }}>Default</th>
                                <th style={{ padding: 8 }}>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {methods.map(m => (
                                <tr key={m.id} style={{ borderTop: "1px solid rgba(255,255,255,0.12)" }}>
                                    <td style={{ padding: 8 }}>{m.label || "—"}</td>
                                    <td style={{ padding: 8 }}>{m.brand}</td>
                                    <td style={{ padding: 8 }}>{m.last4}</td>
                                    <td style={{ padding: 8 }}>{m.expMonth}/{m.expYear}</td>
                                    <td style={{ padding: 8 }}>{m.isDefault ? "Yes" : "No"}</td>
                                    <td style={{ padding: 8, textAlign: "right" }}>
                                        {!m.isDefault && (
                                            <Button onClick={() => makeDefault(m.id)} style={{ marginRight: 8 }}>
                                                Make Default
                                            </Button>
                                        )}
                                        <Button variant="secondary" onClick={() => remove(m.id)}>Delete</Button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </Panel>
            </StackLayout>
        </div>
    );
}
