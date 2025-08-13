FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


SRC_URI:append = " \
    file://host-powercycle \
    file://host-powercycle@.service \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    "

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-reboot@{}.target.requires/obmc-host-shutdown@{}.target \
    "

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
"

SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-reset-chassis-on@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "phosphor-reset-chassis-running@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "obmc-power-start@.service"
SYSTEMD_SERVICE:${PN}-chassis:remove = "obmc-power-stop@.service"

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/host-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
}

FILES:${PN} += " ${systemd_system_unitdir}/*.service"
FILES:${PN} += " ${libexecdir}/${PN}"
