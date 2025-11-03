import {StrictMode, useEffect, useState} from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import {SaltProvider} from "@salt-ds/core";
import "@salt-ds/theme/index.css";
import { ModuleRegistry, AllCommunityModule } from "ag-grid-community";
ModuleRegistry.registerModules([AllCommunityModule]);

function usePrefersColorScheme() {
    const query = "(prefers-color-scheme: dark)";
    const getMode = () =>
        window.matchMedia?.(query).matches ? "dark" : "light";

    const [mode, setMode] = useState(getMode);

    useEffect(() => {
        const mq = window.matchMedia(query);
        const onChange = (e) => setMode(e.matches ? "dark" : "light");

        if (mq.addEventListener) {
            mq.addEventListener("change", onChange);
            return () => mq.removeEventListener("change", onChange);
        }
        // Fallback for very old Safari
        mq.addListener?.(onChange);
        return () => mq.removeListener?.(onChange);
    }, []);

    console.log(mode)
    return mode;
}

function Root() {
    const mode = usePrefersColorScheme();
return (
      <SaltProvider mode={mode} density="medium">
    <App />
      </SaltProvider>
);
}

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <Root />
    </StrictMode>
);

