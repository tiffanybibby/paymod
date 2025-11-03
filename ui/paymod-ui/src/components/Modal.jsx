import { createPortal } from "react-dom";

export default function Modal({ open, onClose, children }) {
    if (!open) return null;
    return createPortal(
        <div
            role="dialog"
            aria-modal="true"
            onClick={onClose}
            style={{
                position: "fixed", inset: 0, background: "rgba(0,0,0,0.4)",
                display: "grid", placeItems: "center", zIndex: 1000
            }}
        >
            <div
                onClick={(e) => e.stopPropagation()}
                style={{
                    width: "min(720px, 92vw)", maxHeight: "85vh", overflow: "auto",
                    borderRadius: 12, padding: 16, background: "var(--salt-container-primary-background)"
                }}
            >
                {children}
            </div>
        </div>,
        document.body
    );
}
