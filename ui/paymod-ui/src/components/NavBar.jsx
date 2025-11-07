import { Link, useLocation, useNavigate } from "react-router-dom";
import { Button, Text, Tooltip } from "@salt-ds/core";
import { SyncIcon } from "@salt-ds/icons";
import { useAuth } from "../context/AuthContext.jsx";
import "../App.css";

const LINKS_AUTHED = [
    { to: "/checkout", label: "Checkout" },
    { to: "/wallet", label: "Wallet" },
    { to: "/admin", label: "Admin" },
    { to: "/dashboard", label: "Dashboard" }
];

const LINKS_PUBLIC = [
    { to: "/admin", label: "Admin" },
    { to: "/dashboard", label: "Dashboard" },
];

export default function NavBar() {
    const { pathname } = useLocation();
    const nav = useNavigate();
    const { user, logout } = useAuth();

    const LINKS = user ? LINKS_AUTHED : LINKS_PUBLIC;

    const displayName = user?.firstName || user?.lastName ? `${user?.firstName ?? ""} ${user?.lastName ?? ""}`.trim() : user?.email;

    function handleLogout() {
        logout();
        nav("/login");
    }

    function handleGlobalRefresh() {
        window.dispatchEvent(new CustomEvent("paymod:refresh"));
    }

    return (
        <header
            className="nav-header"
            role="navigation"
            aria-label="Primary">

            <div className="nav-container">
                <div className="nav-brand">
                    <div className="nav-brand-dot" aria-hidden />
                    <Text styleAs="h3">Paymod</Text>
                </div>

                <nav className="nav-links">
                    {LINKS.map(({ to, label }) => {
                        const active =
                            to === "/" ? pathname === "/" : pathname.startsWith(to);
                        return (
                            <Link
                                key={to}
                                to={to}
                                className={"nav-link" + (active ? " nav-link-active" : "")}
                                aria-current={active ? "page" : undefined}
                            >
                                {label}
                            </Link>
                        );
                    })}
                </nav>

                <div className="nav-right">
                    <Tooltip content="Refresh data">
                        <Button
                            variant="secondary"
                            size="small"
                            onClick={handleGlobalRefresh}
                            aria-label="Refresh data">
                            <SyncIcon aria-hidden />
                        </Button>
                    </Tooltip>
                    <span className="env-badge">Dev</span>
                    {!user ? (
                        <Link to="/login" className="nav-login-link">
                            <Button>Login</Button>
                        </Link>
                    ) : (
                        <>
                            <span
                                className="user-pill"
                                title={user.email}>
                                {displayName}
                            </span>
                            <Button
                                variant="secondary"
                                onClick={handleLogout}>
                                Logout
                            </Button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
