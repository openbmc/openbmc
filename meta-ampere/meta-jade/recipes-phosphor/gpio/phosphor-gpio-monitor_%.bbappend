FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

inherit systemd

RDEPENDS:${PN} += "bash"

SRC_URI += " \
            file://phosphor-multi-gpio-monitor.json \
            file://phosphor-multi-gpio-presence.json \
            file://ampere_scp_failover.sh \
            file://ampere_psu_reset_hotswap.sh \
            file://ampere_scp_failover.service \
            file://psu_hotswap_reset@.service \
           "

SYSTEMD_SERVICE:${PN}-monitor += " \
                                  ampere_scp_failover.service \
                                 "

FILES:${PN}-monitor += " \
                        ${datadir}/${BPN}/phosphor-multi-gpio-monitor.json \
                        ${sbindir}/ampere_scp_failover.sh \
                        ${sbindir}/ampere_psu_reset_hotswap.sh \
                        ${systemd_system_unitdir}/ampere_scp_failover.service \
                        ${systemd_system_unitdir}/psu_hotswap_reset@.service \
                       "

FILES:${PN}-presence += " \
                         ${datadir}/${BPN}/phosphor-multi-gpio-presence.json \
                        "

do_install:append() {
    install -d ${D}${sbindir}
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json ${D}${datadir}/${BPN}/
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json ${D}${datadir}/${BPN}/
    install -m 0755 ${UNPACKDIR}/ampere_scp_failover.sh ${D}${sbindir}/
    install -m 0755 ${UNPACKDIR}/ampere_psu_reset_hotswap.sh ${D}${sbindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/ampere_scp_failover.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/psu_hotswap_reset@.service ${D}${systemd_system_unitdir}/
}
