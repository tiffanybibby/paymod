import {useEffect, useMemo, useState, useLayoutEffect, useRef, useCallback} from "react";
import axios from "axios";
import { Panel, StackLayout, Text } from "@salt-ds/core";
import {ResponsiveContainer, PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, LineChart, Line} from "recharts";
import "../App.css";

const fmtDateISO = (s) => {
    const d = new Date(s);
    return isNaN(d) ? "" : d.toISOString().slice(0, 10);
};
const fmtUSD = (n) => `$${Number(n).toFixed(2)}`;
const fmtUSDCompact = (n) => {
    const v = Number(n);
    if (v >= 1_000_000) return `$${(v / 1_000_000).toFixed(1)}M`;
    if (v >= 1_000) return `$${(v / 1_000).toFixed(0)}k`;
    return `$${v.toFixed(0)}`;
};
const shortenEmail = (e = "") => {
    const [local, domain = ""] = e.split("@");
    return domain ? `${local.length > 9 ? local.slice(0, 9) + "…" : local}@${domain}` : e;
};

// render charts only when they have size
// TODO: seed additional chart data with distinct dates
function useSize() {
    const ref = useRef(null);
    const [size, setSize] = useState({ w: 0, h: 0 });
    useLayoutEffect(() => {
        if (!ref.current) return;
        const ro = new ResizeObserver(([entry]) => {
            const r = entry.contentRect;
            setSize({ w: r.width, h: r.height });
        });
        ro.observe(ref.current);
        return () => ro.disconnect();
    }, []);
    return [ref, size];
}
function ChartPanel({ title, children }) {
    const [ref, size] = useSize();
    return (
        <Panel className="panel" ref={ref}>
            <Text styleAs="h3" style={{ marginBottom: 8 }}>{title}</Text>
            <div className="chart">
                {size.w > 0 && size.h > 0 && (
                    <ResponsiveContainer width="100%" height="100%">
                        {children}
                    </ResponsiveContainer>
                )}
            </div>
        </Panel>
    );
}

export default function Dashboard() {
    const [payments, setPayments] = useState([]);

    const loadPayments = useCallback(async () => {
        try {
            const r = await axios.get("/api/payments");
            setPayments(r.data ?? []);
        } catch (err) {
            console.error("Failed to load payments", err);
        }
    }, []);

    useEffect(() => {
        loadPayments();
    }, [loadPayments]);

    useEffect(() => {
        const handler = () => {
            loadPayments();
        };
        window.addEventListener("paymod:refresh", handler);
        return () => window.removeEventListener("paymod:refresh", handler);
    }, [loadPayments]);

    const byStatus = useMemo(() => {
        const buckets = { PENDING: 0, SUCCESS: 0, FAILED: 0 };
        for (const p of payments) {
            const status = p.status
            if (!status) continue;
            if (buckets[status] === undefined) continue;
            buckets[status] += 1;
        }
        return Object.entries(buckets)
            .filter(([, value]) => value > 0)
            .map(([name, value]) => ({ name, value }));
    }, [payments]);

    const byDay = useMemo(() => {
        const m = new Map();
        for (const p of payments) {
            const day = fmtDateISO(p.createdAt);
            if (!day) continue;
            const status = p.status || p.paymentStatus;
            if (!["PENDING", "SUCCESS", "FAILED"].includes(status)) {
                continue;
            }
            if (!m.has(day)) {
                m.set(day, {
                    date: day,
                    PENDING: 0,
                    SUCCESS: 0,
                    FAILED: 0,
                    totalAmount: 0,
                });
            }
            const row = m.get(day);
            row[status] += 1;
            const amt = Number(p.amount || 0);
            if (!Number.isNaN(amt)) {
                row.totalAmount += amt;
            }
        }
        return Array.from(m.values()).sort((a, b) =>
            a.date.localeCompare(b.date)
        );
    }, [payments]);

    const topCustomers = useMemo(() => {
        const m = new Map();
        for (const p of payments) {
            const email = p.userEmail ?? "Unknown";
            m.set(email, (m.get(email) || 0) + Number(p.amount || 0));
        }
        return Array.from(m.entries())
            .map(([email, totalAmount]) => ({ email, totalAmount }))
            .sort((a, b) => b.totalAmount - a.totalAmount)
            .slice(0, 7);
    }, [payments]);

    const COLORS = ["#69b34c", "#ff4e50", "#f5a623", "#4a90e2", "#bd10e0", "#50e3c2"];
    const AXIS = "#a8a8a8";
    const GRID = "#3a3a3a";
    const smallTick = { fontSize: 10 };
    const TICK_FONTSIZE = 10;
    const LABEL_FONTSIZE = 11;
    const X_TICK_MARGIN = 2;
    const X_AXIS_H = 28;  // space for tick text + label line inside the chart
    const Y_AXIS_W = 44;

    return (
        <div className="container page">
            <StackLayout gap={2}>
                <Text styleAs="h2">Payments Dashboard</Text>
                <div className="dashboard">
                    <ChartPanel title="By Status">
                        <PieChart>
                            <Pie data={byStatus} dataKey="value" nameKey="name" outerRadius={100} label>
                                {byStatus.map((_, i) => (
                                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip />
                        </PieChart>
                    </ChartPanel>
                    <ChartPanel title="Daily Volume (count)">
                        <LineChart data={byDay} margin={{ top: 6, right: 6, bottom: 2, left: 6 }}>
                            <CartesianGrid stroke={GRID} />
                            <XAxis
                                dataKey="date"
                                stroke={AXIS}
                                tick={{ fontSize: TICK_FONTSIZE }}
                                tickMargin={X_TICK_MARGIN}
                                tickLine={false}
                                axisLine={{ stroke: GRID }}
                                interval="preserveStartEnd"
                                height={X_AXIS_H}
                                label={{ value: "Date", position: "insideBottom", offset: -1, fontSize: LABEL_FONTSIZE }}
                            />
                            <YAxis
                                allowDecimals={false}
                                stroke={AXIS}
                                tick={{ fontSize: TICK_FONTSIZE }}
                                tickLine={false}
                                axisLine={{ stroke: GRID }}
                                width={Y_AXIS_W}
                                label={{ value: "Count", angle: -90, position: "insideLeft", fontSize: LABEL_FONTSIZE }}
                            />
                            <Tooltip />
                            <Line type="monotone" dataKey="SUCCESS" name="Success" dot={false} />
                            <Line type="monotone" dataKey="FAILED" name="Failed" dot={false} />
                            <Line type="monotone" dataKey="PENDING" name="Pending" dot={false} />
                        </LineChart>
                    </ChartPanel>
                    <ChartPanel title="Top Customers (by amount)">
                        <BarChart
                            data={topCustomers}
                            layout="vertical"
                            margin={{ top: 6, right: 6, bottom: 2, left: 6 }}
                            barCategoryGap={40}>
                            <CartesianGrid stroke={GRID} />
                            <XAxis
                                type="number"
                                stroke={AXIS}
                                tick={{ fontSize: TICK_FONTSIZE }}
                                tickFormatter={fmtUSDCompact}
                                tickMargin={X_TICK_MARGIN}
                                tickLine={false}
                                axisLine={{ stroke: GRID }}
                                height={X_AXIS_H}
                                label={{ value: "Total amount (USD)", position: "insideBottom", offset: -1, fontSize: LABEL_FONTSIZE }}
                            />
                            <YAxis
                                dataKey="email"
                                type="category"
                                width={120}
                                stroke={AXIS}
                                tick={{ fontSize: TICK_FONTSIZE }}
                                tickFormatter={shortenEmail}
                                tickLine={false}
                                axisLine={{ stroke: GRID }}
                            />
                            <Tooltip formatter={(v) => fmtUSD(v)} />
                            <Bar dataKey="totalAmount" name="Amount" />
                        </BarChart>
                    </ChartPanel>
                    <ChartPanel title="Daily Amount (USD)">
                        <BarChart data={byDay} margin={{ top: 6, right: 6, bottom: 2, left: 6 }}>
                            <CartesianGrid stroke={GRID}/>
                            <XAxis
                                dataKey="date"
                                stroke={AXIS}
                                tick={{ fontSize: TICK_FONTSIZE }}
                                tickMargin={X_TICK_MARGIN}
                                tickLine={false}
                                axisLine={{ stroke: GRID }}
                                interval="preserveStartEnd"
                                height={X_AXIS_H}
                                label={{ value: "Date", position: "insideBottom", offset: -1, fontSize: LABEL_FONTSIZE }}
                            />
                            <YAxis
                                stroke={AXIS}
                                tick={{ fontSize: TICK_FONTSIZE }}
                                tickFormatter={fmtUSDCompact}
                                tickLine={false}
                                axisLine={{ stroke: GRID }}
                                width={Y_AXIS_W}
                                label={{ value: "USD", angle: -90, position: "insideLeft", fontSize: LABEL_FONTSIZE }}
                            />
                            <Tooltip formatter={(v) => fmtUSD(v)} />
                            <Bar dataKey="totalAmount" name="Total Amount" />
                        </BarChart>
                    </ChartPanel>
                </div>
            </StackLayout>
        </div>
    );
}
