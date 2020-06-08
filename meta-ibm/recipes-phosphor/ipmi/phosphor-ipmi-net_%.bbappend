ALT_RMCPP_IFACE_mihawk  = "eth1"
SYSTEMD_SERVICE_${PN}_append_mihawk += " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "

