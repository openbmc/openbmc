SUMMARY = "Phosphor Systemd targets"
DESCRIPTION = "Provides well known Systemd syncronization points for OpenBMC."
HOMEPAGE = "http://github.com/openbmc"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
inherit allarch

SYSTEMD_SERVICE_${PN} = " \
        obmc-mapper.target \
        obmc-fans-ready.target \
        obmc-fan-control.target \
        obmc-standby.target \
        obmc-chassis-stop@.target \
        obmc-chassis-start@.target \
        obmc-power-start-pre@.target \
        obmc-power-start@.target \
        obmc-power-on@.target \
        obmc-power-stop-pre@.target \
        obmc-power-stop@.target \
        obmc-power-off@.target \
        obmc-host-start-pre@.target \
        obmc-host-start@.target \
        obmc-host-started@.target \
        obmc-host-stop-pre@.target \
        obmc-host-stop@.target \
        obmc-host-stopped@.target \
        "

SYSTEMD_GENLINKS_${PN} += "obmc-chassis-start@.target:obmc-chassis-start@[0].target:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-chassis-stop@.target:obmc-chassis-stop@[0].target:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-power-start-pre@.target:obmc-power-start-pre@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-power-start@.target:obmc-power-start@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-power-on@.target:obmc-power-on@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-power-stop-pre@.target:obmc-power-stop-pre@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-power-stop@.target:obmc-power-stop@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-power-off@.target:obmc-power-off@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-host-start-pre@.target:obmc-host-start-pre@[0].target:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-host-start@.target:obmc-host-start@[0].target:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-host-started@.target:obmc-host-started@[0].target:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-host-stop-pre@.target:obmc-host-stop-pre@[0].target:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-host-stop@.target:obmc-host-stop@[0].target:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-host-stopped@.target:obmc-host-stopped@[0].target:OBMC_HOST_INSTANCES"
