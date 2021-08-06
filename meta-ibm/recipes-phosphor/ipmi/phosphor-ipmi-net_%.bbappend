ALT_RMCPP_IFACE:mihawk  = "eth1"
SYSTEMD_SERVICE:${PN}:append:mihawk += " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "

