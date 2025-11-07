import { useState } from "react";
import { StackLayout, Button } from "@salt-ds/core";
import Modal from "./Modal";
import AddUserForm from "./AddUserForm.jsx";
import MakePaymentForm from "./MakePaymentForm.jsx";

export default function QuickActions({ onRefresh }) {
    const [openAdd, setOpenAdd] = useState(false);
    const [openPay, setOpenPay] = useState(false);

    return (
        <>
            <StackLayout direction="row" gap={2} style={{ marginBottom: 12 }} wrap>
                <Button onClick={() => setOpenAdd(true)}>Add User</Button>
                {/*TODO: Enable for admin only with user lookup*/}
                {/*<Button onClick={() => setOpenPay(true)}>Make a Payment</Button>*/}
            </StackLayout>

            <Modal open={openAdd} onClose={() => setOpenAdd(false)}>
                <AddUserForm onClose={() => setOpenAdd(false)} onSuccess={onRefresh} />
            </Modal>

            <Modal open={openPay} onClose={() => setOpenPay(false)}>
                <MakePaymentForm onClose={() => setOpenPay(false)} onSuccess={onRefresh} />
            </Modal>
        </>
    );
}
