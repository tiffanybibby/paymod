import './App.css'
import "ag-grid-community/styles/ag-grid.css";
import "@salt-ds/ag-grid-theme/salt-ag-theme.css";
import { Routes, Route } from "react-router-dom";
import CustomerCheckout from "./screens/CustomerCheckout";
import Receipt from "./screens/Receipt";
import Admin from "./screens/Admin.jsx";
import Dashboard from "./screens/Dashboard.jsx";
import NavBar from "./components/NavBar.jsx";
import Login from "./screens/Login.jsx";
import Wallet from "./screens/Wallet.jsx";
import {useAuth} from "./context/AuthContext.jsx";

export default function App() {
    const { user } = useAuth();
    return (
        <>
            <NavBar/>
            <main style={{padding: "16px 16px 32px"}}>
                <Routes>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/checkout" element={user ? <CustomerCheckout/> : <Login/>}/>
                    <Route path="/wallet" element={user ? <Wallet/> : <Login/>}/>
                    <Route path="/receipt/:id" element={user ? <Receipt/> : <Login/>}/>
                    <Route path="/admin" element={<Admin/>}/>
                    <Route path="*" element={user ? <CustomerCheckout/> : <Login/>}/>
                    <Route path="/dashboard" element={<Dashboard/>}/>
                </Routes>
            </main>
        </>
    );
}
