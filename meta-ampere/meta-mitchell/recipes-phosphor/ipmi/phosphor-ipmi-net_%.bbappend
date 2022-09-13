DEFAULT_RMCPP_IFACE = "eth0"

ALT_RMCPP_IFACE = "eth1"
SYSTEMD_SERVICE:${PN} += " \
        ${PN}@${ALT_RMCPP_IFACE}.service \
        ${PN}@${ALT_RMCPP_IFACE}.socket \
        "
