FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    file://chassis-poweroff@.service \
    file://chassis-poweron \
    file://chassis-poweron@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    "

RDEPENDS:${PN}-discover:append = " bletchley-common-tool"
RDEPENDS:${PN}:append = " bash motor-ctrl"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
}

FILES:${PN}:append= " ${systemd_system_unitdir}"
FILES:${PN}:append= " ${libexecdir}/${PN}"

# Because Bletchley does not have IPMI between Bmc & Host, the Host init
# state will set to Off after Bmc booted. We require an extra service to
# check and set Host state & Chassis power state to correct state before
# doing any power action or power policy restore.

BLETCHLEY_SYS_ST_INIT_CONF_FMT = "bletchley-system-state-init.conf:phosphor-discover-system-state@{0}.service.d/bletchley-system-state-init.conf"
SYSTEMD_OVERRIDE:${PN}-discover += "${@compose_list_zip(d, 'BLETCHLEY_SYS_ST_INIT_CONF_FMT', 'OBMC_HOST_INSTANCES')}"

PACKAGECONFIG:append = " no-warm-reboot"
PACKAGECONFIG:remove = "only-run-apr-on-power-loss"

#======================
# Workaround for bletchley
#======================
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-startmin@{}.target.wants/host-poweron@{}.service \
    obmc-host-stop@{}.target.wants/host-poweroff@{}.service \
"
