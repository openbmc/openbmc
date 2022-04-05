divert(-1)
define(`HOST_MAC_ARG', `ifelse($1, `invalid', `',
            ifelse($1, `', `',
                ` --host-mac "$1"'))')

define(`DEV_MAC_ARG', `ifelse($1, `invalid', `',
            ifelse($1, `', `',
                ` --dev-mac "$1"'))')

divert(0)dnl
dnl
[Unit]
Description=USB Gadget

[Service]
Type=oneshot
RemainAfterExit=yes
ExecStart=M_SCRIPT_INSTALL_DIR/usb_network.sh \
    --product-id "M_BMC_USB_PRODUCT_ID" \
    --product-name "M_BMC_USB_PRODUCT_NAME" \
    --dev-type "M_BMC_USB_TYPE" \
    HOST_MAC_ARG(M_BMC_USB_HOST_MAC) \
    DEV_MAC_ARG(M_BMC_USB_DEV_MAC) \
    --iface-name "M_BMC_USB_IFACE" \
    --bind-device "M_BMC_USB_BIND_DEV"
ExecStop=M_SCRIPT_INSTALL_DIR/usb_network.sh stop \
    --dev-type "M_BMC_USB_TYPE" \
    --iface-name "M_BMC_USB_IFACE"

[Install]
WantedBy=multi-user.target
