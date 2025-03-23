FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

#===============================================================================
# TODO: need to be removed once patch merged.
# meta-facebook: phosphor-state-manager: refactor targets and relationships
# https://gerrit.openbmc.org/c/openbmc/openbmc/+/69903

PACKAGECONFIG:remove = "only-run-apr-on-power-loss"

# The host-graceful-poweroff service replaces the `pldmSoftPowerOff` and
# `xyz.openbmc_project.Ipmi.Internal.SoftPowerOff` services to gracefully
# shutdown the host.
HOST_DEFAULT_TARGETS:append = " \
    obmc-host-shutdown@{}.target.requires/host-graceful-poweroff@{}.service \
    "

# The "warm-reboot" is what does the graceful shutdown operation and the
# normal "reboot" is suppose to do a forced reboot.  `host-shutdown` is used
# as part of the graceful shutdown path, which we want to skip in a normal
# reboot, so remove the dependency.  The `phosphor-reboot-host@.service` does
# continue to be in `obmc-host-reboot` requirements and it depends on
# `obmc-host-stop`, which will initiate a `host-force-poweroff`.
HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-reboot@{}.target.requires/obmc-host-shutdown@{}.service \
    "

# The host-poweron service is the equivalent to the start_host@ service
# from meta-openpower.
HOST_DEFAULT_TARGETS:append = " \
    obmc-host-startmin@{}.target.requires/host-poweron@{}.service \
    "

# The host-stop service is called as a side-effect of force-warm-reboot and is
# expected to force-stop the processors.  They will be restarted in the
# subsequent `obmc-host-reboot` (which calls `host-startmin`)
HOST_DEFAULT_TARGETS:append = " \
    obmc-host-stop@{}.target.requires/host-force-poweroff@{}.service \
    "

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-warm-reboot@{}.target.requires/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
    obmc-host-warm-reboot@{}.target.requires/obmc-host-force-warm-reboot@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/obmc-host-stop@{}.target \
    obmc-host-force-warm-reboot@{}.target.requires/phosphor-reboot-host@{}.service \
    obmc-host-graceful-quiesce@{}.target.wants/pldmSoftPowerOff.service \
    "

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-warm-reboot@{}.target.wants/host-powerreset@{}.service \
    "

# Add services for the chassis power operations.
CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-poweron@{}.target.requires/chassis-poweron@{}.service \
    obmc-chassis-powercycle@{}.target.requires/chassis-powercycle@{}.service \
    "

# We don't use the obmc-power-start or obmc-power-stop, which use the
# `org.openbmc` interface but instead install our own chassis-poweron and
# chassis-poweroff.
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweron@{}.target.requires/obmc-power-start@{}.service \
    obmc-chassis-poweroff@{}.target.requires/obmc-power-stop@{}.service \
    "

# The obmc-chassis-powerreset target is used to determine the state of the host
# when the BMC resets (to handle cases where the BMC reset while the host is
# running).  The default implementation of these relies on `org.openbmc`
# interfaces we do not implement, so we need to remove them.
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-on@{}.service \
    obmc-chassis-powerreset@{}.target.requires/phosphor-reset-chassis-running@{}.service \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*.service"
FILES:${PN} += "${libexecdir}/${PN}"

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweron@{}.target.wants/chassis-poweron@{}.service \
    obmc-chassis-hard-poweroff@{}.target.wants/chassis-poweroff@{}.service \
    obmc-chassis-powercycle@{}.target.wants/chassis-powercycle@{}.service \
"

HOST_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.wants/host-poweroff@{}.service \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
    obmc-host-reboot@{}.target.wants/host-powercycle@{}.service \
"
#===============================================================================

RDEPENDS:${PN}:append = " bash"
PACKAGECONFIG:append = " host-gpio"

SRC_URI:append = " \
    file://chassis-power-state-init \
    file://chassis-power-state-init.conf \
    file://chassis-powercycle \
    file://chassis-powercycle@.service \
    file://chassis-poweron \
    file://chassis-poweron@.service \
    file://host-force-poweroff \
    file://host-force-poweroff@.service \
    file://host-graceful-poweroff \
    file://host-graceful-poweroff@.service \
    file://host-poweron \
    file://host-poweron@.service \
    file://host-powerreset \
    file://host-powerreset@.service \
    file://power-cmd \
    file://phosphor-wait-power-off@.service \
    "

#We need to ensure that the chassis power is always on.
CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-host-shutdown@{}.target.requires/obmc-chassis-poweroff@{}.target \
    "
HARD_OFF_TMPL_CTRL=""
HARD_OFF_TGTFMT_CTRL=""
HARD_OFF_FMT_CTRL=""
HARD_OFF_INSTFMT_CTRL=""

#Remove unexpected ChassisPowerOnStarted log at host first start
RRECOMMENDS:${PN}-chassis:remove = " ${PN}-chassis-poweron-log"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/chassis-power-state-init ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/chassis-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/chassis-powercycle ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-force-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-graceful-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-poweron ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/host-powerreset ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-cmd ${D}${libexecdir}/${PN}/
}

SYSTEMD_OVERRIDE:${PN}-host += "chassis-power-state-init.conf:xyz.openbmc_project.State.Host@0.service.d/chassis-power-state-init.conf"
