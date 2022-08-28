FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEFAULT_TARGETS = " \
    multi-user.target.requires/obmc-host-reset@{}.target \
    obmc-chassis-poweron@{}.target.wants/chassis-poweron@{}.service \
    obmc-chassis-hard-poweroff@{}.target.wants/chassis-poweroff@{}.service \
    obmc-host-shutdown@{}.target.wants/host-poweroff@{}.service \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
    obmc-host-reboot@{}.target.wants/host-powercycle@{}.service \
"

SRC_URI:append:greatlakes = " \
    file://chassis-poweroff@.service \
    file://chassis-poweron@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    file://host-powercycle@.service \
    file://chassis-poweroff \
    file://chassis-poweron \
    file://host-poweroff \
    file://host-poweron \
    file://host-powercycle \
    file://power-cmd \
    "

RDEPENDS:${PN}:append:greatlakes = " bash"

do_install:append:greatlakes() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}
    install -m 0777 ${WORKDIR}/chassis-poweroff ${D}${libexecdir}/
    install -m 0777 ${WORKDIR}/chassis-poweron ${D}${libexecdir}/
    install -m 0777 ${WORKDIR}/host-poweroff ${D}${libexecdir}/
    install -m 0777 ${WORKDIR}/host-poweron ${D}${libexecdir}/
    install -m 0777 ${WORKDIR}/host-powercycle ${D}${libexecdir}/
    install -m 0777 ${WORKDIR}/power-cmd ${D}${libexecdir}/
}
FILES:${PN} += " /lib/systemd/system/*.service"
