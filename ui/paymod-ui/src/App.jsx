import { useState, useEffect } from 'react'
import './App.css'
import axios from "axios";
import { StackLayout, Text, FormField, Input, Button } from "@salt-ds/core";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "@salt-ds/ag-grid-theme/salt-ag-theme.css";

export default function App() {
    const [payments, setPayments] = useState([]);
    const [filter, setFilter] = useState("");
    const [selectedId, setSelectedId] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        (async () => {
            try {
                const r = await axios.get("/api/payments");
                setPayments(r.data ?? []);
            } catch (e) {
                setError("Unable to load payments");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const columns = [
        { field: "id", headerName: "ID", maxWidth: 100 },
        { field: "userEmail", headerName: "User", minWidth: 240, flex: 1 },
        { field: "amount", headerName: "Amount", valueFormatter: p => `$${Number(p.value/100).toFixed(2)}` },
        { field: "currency", headerName: "CCY", maxWidth: 100 },
        { field: "paymentStatus", headerName: "Status", minWidth: 130 },
        { field: "createdAt", headerName: "Created", minWidth: 220,
            valueFormatter: p => new Date(p.value).toLocaleString()
        },
    ];

    const rows = payments
        .filter(payment =>
            filter
                ? String(payment.id).includes(filter) ||
                (payment.user?.email || "").toLowerCase().includes(filter.toLowerCase())
                : true
        )
        .map(payment => ({
            id: payment.id,
            userEmail: payment.user?.email ?? "",
            amount: payment.amount,
            currency: payment.currency,
            paymentStatus: payment.paymentStatus,
            createdAt: payment.createdAt
        }));

    async function openHistory(id) {
        setSelectedId(id);
        const r = await axios.get(`/api/payments/${id}/history`);
        setHistory(r.data || []);
    }

    return (
        <StackLayout gap={2} style={{ padding: 16 }}>
            <Text styleAs="h2">Paymod • Payments</Text>

            <StackLayout direction="row" gap={2} align="center">
                <FormField label="Filter">
                    <Input value={filter} onChange={(e) => setFilter(e.target.value)} placeholder="Search by ID or email" />
                </FormField>
                <Button onClick={() => setFilter("")}>Clear</Button>
            </StackLayout>

            <div style={{ maxWidth: "1200px", width: "95vw", margin: "24px auto" }}>
            <div className="ag-theme-salt-ag" style={{ height: "70vh", width: "100%" }}>
                <AgGridReact
                    gridOptions={{ theme: "legacy" }}
                    columnDefs={columns}
                    rowData={rows}
                    onRowClicked={(e) => openHistory(e.data.id)}
                />
            </div>
            </div>

            {selectedId && (
                <>
                    <Text styleAs="h3" style={{ marginTop: 12 }}>History for Payment #{selectedId}</Text>
                    <ul style={{ marginTop: 8 }}>
                        {paymentHistory.map((history) => (
                            <li key={history.id}>
                                <strong>{history.eventType}</strong> → {history.newStatus ?? "-"} at {history.occurredAt}
                            </li>
                        ))}
                    </ul>
                </>
            )}
        </StackLayout>
    );
}
