import { useState, useEffect, useMemo } from "react";
import "../App.css";
import axios from "axios";
import {StackLayout, Text, FormField, Input, Button, Panel, StatusIndicator} from "@salt-ds/core";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "@salt-ds/ag-grid-theme/salt-ag-theme.css";
import QuickActions from "../components/QuickActions.jsx";

export default function Admin() {
    const [payments, setPayments] = useState([]);
    const [filter, setFilter] = useState("");
    const [selectedId, setSelectedId] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [processingId, setProcessingId] = useState(null);
    const [error, setError] = useState("");

    const loadPayments = async () => {
        try {
            setLoading(true);
            setError("");
            const r = await axios.get("/api/payments");
            setPayments(r.data ?? []);
        } catch (e) {
            console.error(e);
            setError("Unable to load payments");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadPayments();
    }, []);

    useEffect(() => {
        const handler = () => {
            loadPayments();
            if (selectedId) {
                openHistory(selectedId);
            }
        };
        window.addEventListener("paymod:refresh", handler);
        return () => window.removeEventListener("paymod:refresh", handler);
    }, [selectedId]);

    const StatusCell = (p) => {
        const s = p.value;
        const status = s === "SUCCESS" ? "success" : s === "FAILED" ? "error" : "info";
        return <StatusIndicator status={status}>{s}</StatusIndicator>;
    };

    const handleProcess = async (id) => {
        try {
            setProcessingId(id);
            setError("");
            const { data: updated } = await axios.put(`/api/payments/${id}/process`);
            setPayments((prev) => prev.map((p) => (p.id === updated.id ? updated : p)));
            if (selectedId === id) {
                await openHistory(id);
            }
        } catch (e) {
            console.error(e);
            setError("Unable to process payment");
        } finally {
            setProcessingId(null);
        }
    };

    const ActionsCell = (params) => {
        const row = params.data;
        const status = row.paymentStatus;
        if (status !== "PENDING") {
            return <span style={{ opacity: 0.4 }}>—</span>;
        }
        const busy = processingId === row.id;
        return (
            <Button
                variant="cta"
                size="small"
                disabled={busy}
                onClick={(e) => {
                    e.stopPropagation();
                    handleProcess(row.id);
                }}
            >
                {busy ? "Processing..." : "Process"}
            </Button>
        );
    };

    const rows = useMemo(() => {
        return (payments ?? [])
            .filter((payment) => {
                if (!filter) return true;
                const needle = filter.toLowerCase();
                return (
                    String(payment.id).includes(filter) ||
                    (payment.userEmail || "").toLowerCase().includes(needle)
                );
            })
            .map((payment) => {
                const status = payment.status ?? payment.paymentStatus;
                return {
                    id: payment.id,
                    userEmail: payment.userEmail ?? "",
                    amount: payment.amount,
                    currency: payment.currency || "USD",
                    paymentStatus: status,
                    createdAt: payment.createdAt,
                };
            });
    }, [payments, filter]);

    const columns = useMemo(
        () => [
            { field: "id", headerName: "ID", maxWidth: 90 },
            { field: "userEmail", headerName: "User", minWidth: 220, flex: 1 },
            {
                field: "amount",
                headerName: "Amount",
                maxWidth: 140,
                valueFormatter: (p) => p.value != null ? `$${Number(p.value).toFixed(2)}` : "$0.00",
            },
            { field: "currency", headerName: "CCY", maxWidth: 90 },
            {
                field: "paymentStatus",
                headerName: "Status",
                maxWidth: 120,
                cellRenderer: StatusCell,
            },
            {
                field: "createdAt",
                headerName: "Created",
                minWidth: 200,
                valueFormatter: (p) => p.value ? new Date(p.value).toLocaleString() : "",
            },
            {
                headerName: "Actions",
                field: "actions",
                maxWidth: 160,
                cellRenderer: ActionsCell,
            },
        ],
        [processingId]
    );

    async function openHistory(id) {
        setSelectedId(id);
        try {
            const r = await axios.get(`/api/payments/${id}/history`);
            setHistory(r.data || []);
        } catch (e) {
            console.error(e);
            setHistory([]);
        }
    }

    return (
        <div className="container page">
        <StackLayout gap={2} style={{ padding: 16 }}>
            <Text styleAs="h2">Paymod • Admin</Text>
            <StackLayout direction="row" gap={2} align="center">
                <FormField label="Filter">
                    <Input
                        value={filter}
                        onChange={(e) => setFilter(e.target.value)}
                        placeholder="Search by ID or email"
                    />
                </FormField>
                <Button onClick={() => setFilter("")}>Clear</Button>
                {error && (
                    <Text style={{ color: "#ff4e50", marginLeft: 8 }}>{error}</Text>
                )}
            </StackLayout>
                <QuickActions onRefresh={loadPayments} />
                <div
                    className="ag-theme-salt-ag"
                    style={{ height: "70vh", width: "100%" }}>
                    <AgGridReact
                        gridOptions={{ theme: "legacy" }}
                        columnDefs={columns}
                        rowData={rows}
                        rowSelection="single"
                        onRowClicked={(e) => openHistory(e.data.id)}
                        overlayLoadingTemplate={loading ? '<span class="ag-overlay-loading-center">Loading...</span>' : undefined}/>
                </div>
            {selectedId && (
                <Panel style={{ marginTop: 16, padding: 12 }}>
                    <Text styleAs="h3" style={{ marginTop: 12 }}>
                        History for Payment #{selectedId}
                    </Text>
                    <ul style={{ marginTop: 8 }}>
                        {history.map((h) => (
                            <li key={h.id}>
                                <strong>{h.eventType}</strong> → {h.newStatus ?? "-"} at{" "}
                                {h.occurredAt}
                            </li>
                        ))}
                        {history.length === 0 && (
                            <li style={{ opacity: 0.7 }}>
                                No history events for this payment yet.
                            </li>
                        )}
                    </ul>
                </Panel>
            )}
        </StackLayout>
        </div>
    );
}
