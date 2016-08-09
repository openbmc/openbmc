SUMMARY = "Phosphor Systemd targets"
DESCRIPTION = "Provides well known Systemd syncronization points for OpenBMC."
HOMEPAGE = "http://github.com/openbmc"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license
inherit allarch

SYSTEMD_SERVICE_${PN} = " \
        obmc-mapper.target \
        obmc-standby-reached.target \
        obmc-standby.target \
        obmc-chassis@.target \
        obmc-powered-on@.target \
        "

SYSTEMD_GENLINKS_${PN} = "obmc-powered-on@.target:obmc-powered-on@[0].target:OBMC_POWER_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "obmc-chassis@.target:obmc-chassis@[0].target:OBMC_CHASSIS_INSTANCES"
