import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import {Panel, StackLayout, Text, StatusIndicator, Button,} from "@salt-ds/core";
import { useAuth } from "../context/AuthContext.jsx";

export default function Receipt() {
    const { id } = useParams();
    const { authHeaders } = useAuth();

    const [payment, setPayment] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        if (!id) {
            setError("Missing payment id.");
            setLoading(false);
            return;
        }
        let cancelled = false;
        (async () => {
            try {
                setLoading(true);
                setError("");
                const [paymentRes, historyRes] = await Promise.all([
                    axios.get(`/api/payments/${id}`, { headers: authHeaders }),
                    axios.get(`/api/payments/${id}/history`, { headers: authHeaders }),
                ]);
                if (cancelled) return;
                const paymentData = paymentRes.data ?? null;
                const historyData = historyRes.data ?? [];
                if (!paymentData) {
                    setError(`No payment found for ID ${id}.`);
                }
                setPayment(paymentData);
                setHistory(historyData);
            } catch (e) {
                if (cancelled) return;
                console.error("Error loading receipt", e);
                setError("Unable to load receipt details.");
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        })();
        return () => {
            cancelled = true;
        };
    }, [id]);

    const status = payment?.status ?? "UNKNOWN";

    const statusIndicator =
        status  === "SUCCESS" ? "success" : status === "FAILED" ? "error" : "info";

    const fmtAmount = (v) => {
        if (v == null) return "$0.00";
        const n = Number(v);
        return Number.isNaN(n) ? String(v) : `$${n.toFixed(2)}`;
    };

    return (
        <div className="container page">
            <div className="page-narrow">
            <Panel style={{ padding: 16 }}>
                <StackLayout gap={2}>
                    <Text styleAs="h2">Payment Receipt</Text>
                    {loading && (
                        <Text style={{ opacity: 0.8 }}>Loading receipt…</Text>
                    )}
                    {!loading && error && (
                        <Text style={{ color: "#ff4e50" }}>{error}</Text>
                    )}
                    {!loading && !error && !payment && (
                        <Text>No payment found for ID {id}.</Text>
                    )}
                    {!loading && payment && (
                        <>
                            <Panel
                                style={{
                                    padding: 12,
                                    borderRadius: 8,
                                    border: "1px solid rgba(255,255,255,0.06)",
                                }}>
                                <StackLayout gap={1}>
                                    <Text styleAs="h3">Summary</Text>
                                    <Text
                                        style={{ fontSize: 12, opacity: 0.8 }}>
                                        Payment ID: {payment.id}
                                    </Text>
                                    <Text>
                                        Amount:{" "}
                                        <strong>
                                            {fmtAmount(payment.amount)}{" "}
                                            {payment.currency || "USD"}
                                        </strong>
                                    </Text>
                                    <StatusIndicator status={statusIndicator}>
                                        {status === "SUCCESS" ? "Payment succeeded" : status === "FAILED" ? "Payment failed" : status === "PENDING" ? "Payment processing" : status}
                                    </StatusIndicator>
                                    {payment.userEmail && (
                                        <Text style={{ fontSize: 12, opacity: 0.8 }}>
                                            Billed to: {payment.userEmail}
                                        </Text>
                                    )}
                                    {payment.createdAt && (
                                        <Text style={{ fontSize: 12, opacity: 0.8 }}>
                                            Created at:{" "}
                                            {new Date(payment.createdAt).toLocaleString()}
                                        </Text>
                                    )}
                                </StackLayout>
                            </Panel>
                            <Panel
                                style={{
                                    marginTop: 12,
                                    padding: 12,
                                    borderRadius: 8,
                                    border: "1px solid rgba(255,255,255,0.06)",
                                }}>
                                <Text styleAs="h3">Event history</Text>
                                {history.length === 0 ? (
                                    <Text style={{ marginTop: 4, opacity: 0.7 }}>
                                        No history events recorded for this payment yet.
                                    </Text>
                                ) : (
                                    <ul
                                        style={{
                                            marginTop: 8,
                                            paddingLeft: 18,
                                            fontSize: 13,
                                        }}>
                                        {history.map((h) => (
                                            <li key={h.id}>
                                                <strong>{h.eventType}</strong>{" "}
                                                {h.oldStatus && (
                                                    <>
                                                        {h.oldStatus} →{" "}
                                                        {h.newStatus ?? "-"}{" "}
                                                    </>
                                                )}
                                                {!h.oldStatus && h.newStatus && (
                                                    <>Status: {h.newStatus} </>
                                                )}
                                                {h.occurredAt && (
                                                    <span
                                                        style={{
                                                            opacity: 0.75,
                                                            marginLeft: 4,
                                                        }}>
                                                        at{" "}
                                                        {new Date(h.occurredAt).toLocaleString()}
                                                    </span>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </Panel>
                        </>
                    )}
                    <div
                        style={{
                            marginTop: 12,
                            display: "flex",
                            gap: 8,
                        }}>
                        <Link to="/checkout">
                            <Button variant="secondary">
                                Back to checkout
                            </Button>
                        </Link>
                        <Link to="/admin">
                            <Button variant="secondary">
                                View in admin
                            </Button>
                        </Link>
                    </div>
                </StackLayout>
            </Panel>
        </div>
        </div>
    );
}
