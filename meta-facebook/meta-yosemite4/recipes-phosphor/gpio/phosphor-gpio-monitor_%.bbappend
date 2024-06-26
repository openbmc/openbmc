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
    install -m 0644 ${UNPACKDIR}/yosemite4-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/set-button-sled.service ${D}${systemd_system_unitdir}/set-button-sled.service
    install -m 0644 ${UNPACKDIR}/probe-slot-device@.service ${D}${systemd_system_unitdir}/probe-slot-device@.service
    install -m 0644 ${UNPACKDIR}/rescan-fru-ocp-setting@.service ${D}${systemd_system_unitdir}/rescan-fru-ocp-setting@.service
    install -m 0644 ${UNPACKDIR}/slot-hot-plug@.service ${D}${systemd_system_unitdir}/slot-hot-plug@.service
    install -m 0644 ${UNPACKDIR}/setup-nic-endpoint-slot@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/reconfig-net-interface@.service  ${D}${systemd_system_unitdir}/reconfig-net-interface@.service
    install -m 0644 ${UNPACKDIR}/remove-nic-endpoint-slot@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/rescan-wf-bic@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/slot-hsc-fault@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/fan-board-efuse-fault@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/disable-i3c-hub@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/check-interrupt@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/nic-power-fault@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/en-i3c-hub-scan-fru@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/slot-plug-in@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/slot-unplug@.service ${D}${systemd_system_unitdir}/
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/probe-slot-device ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/reconfig-net-interface ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/rescan-fru-ocp-setting ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/rescan-wf-bic ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/slot-hsc-fault ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/fan-board-efuse-fault ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/disable-i3c-hub ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/check-interrupt ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/nic-power-fault ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/en-i3c-hub-scan-fru ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/slot-plug-in ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/slot-unplug ${D}${libexecdir}/${PN}/
    install -d ${D}/${bindir}
    install -m 0755 ${UNPACKDIR}/configure-nic-mctp-endpoint ${D}/${bindir}/
}
