FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "install-utils"
PACKAGECONFIG:append = " run-apr-on-watchdog-reset"

HOST_DEFAULT_TARGETS:append = " \
    obmc-host-shutdown@{}.target.wants/host-poweroff@{}.service \
    obmc-host-start@{}.target.wants/host-poweron@{}.service \
    obmc-host-reboot@{}.target.wants/host-powercycle@{}.service \
    obmc-host-force-warm-reboot@{}.target.wants/host-powerreset@{}.service \
"

CHASSIS_DEFAULT_TARGETS:append = " \
    obmc-chassis-poweron@{}.target.wants/chassis-poweron@{}.service \
    obmc-chassis-hard-poweroff@{}.target.wants/chassis-poweroff@{}.service \
    obmc-chassis-powercycle@{}.target.wants/chassis-powercycle@{}.service \
"

CHASSIS_DEFAULT_TARGETS:remove = " \
    obmc-chassis-poweroff@{}.target.requires/obmc-powered-off@{}.service \
"

FILES:${PN} += " ${systemd_system_unitdir}/*.service"
