ALT_RMCPP_IFACE:npcm8xx = "eth1"
SYSTEMD_SERVICE:${PN}:append:npcm8xx := " \
        ${PN}@${ALT_RMCPP_IFACE}.service \
        ${PN}@${ALT_RMCPP_IFACE}.socket \
        "
