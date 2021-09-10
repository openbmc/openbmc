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
Description=USB ECM Gadget
After=phosphor-ipmi-host.service

[Service]
Type=oneshot
RemainAfterExit=yes
ExecStart=M_SCRIPT_INSTALL_DIR/usb_network.sh \
    --product-id "M_BMC_USB_ECM_PRODUCT_ID" \
    --product-name "M_BMC_USB_ECM_PRODUCT_NAME" \
    HOST_MAC_ARG(M_BMC_USB_ECM_HOST_MAC) \
    DEV_MAC_ARG(M_BMC_USB_ECM_DEV_MAC) \
    --bind-device "M_BMC_USB_ECM_BIND_DEV"
ExecStop=M_SCRIPT_INSTALL_DIR/usb_network.sh stop

[Install]
WantedBy=multi-user.target
