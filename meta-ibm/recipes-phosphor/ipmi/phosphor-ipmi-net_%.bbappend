ALT_RMCPP_IFACE:mihawk = "eth1"
SYSTEMD_SERVICE:${PN}:append:mihawk = " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "

ALT_RMCPP_IFACE:p10bmc = "eth1"
SYSTEMD_SERVICE:${PN}:append:p10bmc = " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "
