FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://yosemite4-phosphor-multi-gpio-monitor.json \
            file://configure-nic-mctp-endpoint \
            file://setup-nic-endpoint-slot@.service \
            file://remove-nic-endpoint-slot@.service \
            file://set-button-sled.service \
            file://probe-slot-device@.service \
            file://probe-slot-device \
            file://reconfig-net-interface@.service \
            file://reconfig-net-interface \
            file://rescan-fru-ocp-setting@.service \
            file://rescan-fru-ocp-setting \
            file://slot-hot-plug@.service \
            file://rescan-wf-bic \
            file://rescan-wf-bic@.service \
            file://slot-hsc-fault \
            file://slot-hsc-fault@.service \
            file://fan-board-efuse-fault \
            file://fan-board-efuse-fault@.service \
            file://disable-i3c-hub \
            file://disable-i3c-hub@.service \
            file://check-interrupt \
            file://check-interrupt@.service \
            file://nic-power-fault \
            file://nic-power-fault@.service \
            file://en-i3c-hub-scan-fru \
            file://en-i3c-hub-scan-fru@.service \
            file://slot-plug-in@.service \
            file://slot-plug-in \
            file://slot-unplug@.service \
            file://slot-unplug \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    set-button-sled.service \
    probe-slot-device@.service \
    rescan-fru-ocp-setting@.service \
    slot-hot-plug@.service \
    setup-nic-endpoint-slot@.service \
    remove-nic-endpoint-slot@.service \
    rescan-wf-bic@.service \
    slot-hsc-fault@.service \
    fan-board-efuse-fault@.service \
    nic-power-fault@.service \
    reconfig-net-interface@.service \
    disable-i3c-hub@.service \
    check-interrupt@.service \
    en-i3c-hub-scan-fru@.service \
    slot-plug-in@.service \
    slot-unplug@.service \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${WORKDIR}/yosemite4-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${WORKDIR}/set-button-sled.service ${D}${systemd_system_unitdir}/set-button-sled.service
    install -m 0644 ${WORKDIR}/probe-slot-device@.service ${D}${systemd_system_unitdir}/probe-slot-device@.service
    install -m 0644 ${WORKDIR}/rescan-fru-ocp-setting@.service ${D}${systemd_system_unitdir}/rescan-fru-ocp-setting@.service
    install -m 0644 ${WORKDIR}/slot-hot-plug@.service ${D}${systemd_system_unitdir}/slot-hot-plug@.service
    install -m 0644 ${WORKDIR}/setup-nic-endpoint-slot@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/reconfig-net-interface@.service  ${D}${systemd_system_unitdir}/reconfig-net-interface@.service
    install -m 0644 ${WORKDIR}/remove-nic-endpoint-slot@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/rescan-wf-bic@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/slot-hsc-fault@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/fan-board-efuse-fault@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/disable-i3c-hub@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/check-interrupt@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/nic-power-fault@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/en-i3c-hub-scan-fru@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/slot-plug-in@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/slot-unplug@.service ${D}${systemd_system_unitdir}/
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/probe-slot-device ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/reconfig-net-interface ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/rescan-fru-ocp-setting ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/rescan-wf-bic ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/slot-hsc-fault ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/fan-board-efuse-fault ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/disable-i3c-hub ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/check-interrupt ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/nic-power-fault ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/en-i3c-hub-scan-fru ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/slot-plug-in ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/slot-unplug ${D}${libexecdir}/${PN}/
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/configure-nic-mctp-endpoint ${D}/${bindir}/
}
