import { createContext, useContext, useMemo, useState } from "react";

const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        const raw = localStorage.getItem("pm.user");
        return raw ? JSON.parse(raw) : null;
    });
    const login = (u) => { setUser(u); localStorage.setItem("pm.user", JSON.stringify(u)); };
    const logout = () => { setUser(null); localStorage.removeItem("pm.user"); };

    const authHeaders = useMemo(() => (user?.id ? { "X-User-ID": String(user.id) } : {}), [user?.id]);

    return <AuthCtx.Provider value={{ user, authHeaders, login, logout }}>{children}</AuthCtx.Provider>;
}
export function useAuth() { return useContext(AuthCtx); }
