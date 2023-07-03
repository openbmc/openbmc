FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN}-monitor += "bash"
RDEPENDS:${PN} += "bash"

SRC_URI += " \
            file://phosphor-multi-gpio-monitor.json \
            file://ampere_scp_failover.sh \
            file://ampere_psu_reset_hotswap.sh \
           "

SYSTEMD_SERVICE:${PN}-monitor += " \
                                  ampere_scp_failover.service \
                                  psu_hotswap_reset@.service \
                                 "

FILES:${PN}-monitor += " \
                        ${datadir}/${PN}/phosphor-multi-gpio-monitor.json \
                        /usr/sbin/ampere_scp_failover.sh \
                        /usr/sbin/ampere_psu_reset_hotswap.sh \
                       "

do_install:append() {
    install -d ${D}${sbindir}
    install -m 0644 ${WORKDIR}/phosphor-multi-gpio-monitor.json ${D}${datadir}/${PN}/
    install -m 0755 ${WORKDIR}/ampere_scp_failover.sh ${D}${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_psu_reset_hotswap.sh ${D}${sbindir}/
}
