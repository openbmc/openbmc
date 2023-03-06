FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:bletchley = " \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    file://chassis-poweroff@.service \
    file://chassis-poweron@.service \
    file://host-poweroff@.service \
    file://host-poweron@.service \
    "

RDEPENDS:${PN}-discover:append:bletchley = " bletchley-common-tool"
RDEPENDS:${PN}:append:bletchley = " bash motor-ctrl"

do_install:append:bletchley() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0777 ${WORKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
}

FILES:${PN}:append:bletchley = " ${systemd_system_unitdir}"
FILES:${PN}:append:bletchley = " ${libexecdir}/${PN}"

# Because Bletchley does not have IPMI between Bmc & Host, the Host init
# state will set to Off after Bmc booted. We require an extra service to
# check and set Host state & Chassis power state to correct state before
# doing any power action or power policy restore.

BLETCHLEY_SYS_ST_INIT_CONF_FMT = "bletchley-system-state-init.conf:phosphor-discover-system-state@{0}.service.d/bletchley-system-state-init.conf"
SYSTEMD_OVERRIDE:${PN}-discover:bletchley += "${@compose_list_zip(d, 'BLETCHLEY_SYS_ST_INIT_CONF_FMT', 'OBMC_HOST_INSTANCES')}"

#======================
# Workaround for bletchley
#======================
CHASSIS_DEFAULT_TARGETS:remove:bletchley = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-startmin@{}.target.wants/host-poweron@{}.service \
    obmc-host-stop@{}.target.wants/host-poweroff@{}.service \
"