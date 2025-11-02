import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import {SaltProvider} from "@salt-ds/core";
import "@salt-ds/theme/index.css";

createRoot(document.getElementById('root')).render(
  <StrictMode>
      <SaltProvider mode="light" density="high">
    <App />
      </SaltProvider>
  </StrictMode>,
)
