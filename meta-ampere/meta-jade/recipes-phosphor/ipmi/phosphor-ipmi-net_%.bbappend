DEFAULT_RMCPP_IFACE = "eth1"

ALT_RMCPP_IFACE = "eth0"
SYSTEMD_SERVICE_${PN} += " \
        ${PN}@${ALT_RMCPP_IFACE}.service \
        ${PN}@${ALT_RMCPP_IFACE}.socket \
        "
