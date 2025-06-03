FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
           file://ampere_overtemp.sh \
           file://ampere_hightemp.sh \
           "

RDEPENDS:${PN}-monitor:append = " bash"

SYSTEMD_SERVICE:${PN}-monitor += " \
                                  ampere-host-shutdown-ack@.service \
                                  ampere_overtemp@.service \
                                  ampere_hightemp_start@.service \
                                  ampere_hightemp_stop@.service \
                                  ampere_psu_power_restore@.service \
                                  ampere_psu_power_lost@.service \
                                 "

FILES:${PN}-monitor += " \
                        /usr/sbin/ampere_overtemp.sh \
                        /usr/sbin/ampere_hightemp.sh \
                       "

SYSTEMD_LINK:${PN}-monitor:append = " ../phosphor-multi-gpio-monitor.service:multi-user.target.requires/phosphor-multi-gpio-monitor.service"

do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${UNPACKDIR}/ampere_overtemp.sh ${D}/${sbindir}/
    install -m 0755 ${UNPACKDIR}/ampere_hightemp.sh ${D}/${sbindir}/
}
