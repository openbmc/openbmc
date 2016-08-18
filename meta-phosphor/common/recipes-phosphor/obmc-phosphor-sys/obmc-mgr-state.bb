SUMMARY = "OpenBMC state manager"
DESCRIPTION = "OpenBMC state manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-systemd

VIRTUAL-RUNTIME_skeleton_workbook ?= ""
STANDBY_OBJECTS ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-subprocess \
        python-pygobject \
        "

SKELETON_DIR = "pystatemgr"
SYSTEMD_SERVICE_${PN} += "obmc-mgr-state.service obmc-mgr-state.target"
SYSTEMD_GENLINKS_${PN} += "../mapper-wait@.service:obmc-mgr-state.target.wants/mapper-wait@-org-openbmc-settings-host[0].service:OBMC_HOST_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../mapper-wait@.service:obmc-mgr-state.target.wants/mapper-wait@-org-openbmc-control-chassis[0].service:OBMC_CHASSIS_INSTANCES"
SYSTEMD_GENLINKS_${PN} += "../mapper-wait@.service:obmc-mgr-state.target.wants/mapper-wait@-org-openbmc-control-power[0].service:OBMC_POWER_INSTANCES"
