RMCPP_EXTRA = "eth1"
SYSTEMD_SERVICE_${PN} += " \
        ${PN}@${RMCPP_EXTRA}.service \
        ${PN}@${RMCPP_EXTRA}.socket \
        "
