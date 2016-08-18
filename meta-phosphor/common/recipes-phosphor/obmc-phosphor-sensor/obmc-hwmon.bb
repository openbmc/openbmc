SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-systemd

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-shell \
        python-pygobject \
        pyphosphor \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pyhwmon"
SYSTEMD_GENLINKS_${PN} += "../mapper-wait@.service:obmc-fans-ready.target.wants/mapper-wait@-org-openbmc-sensors-speed-fan[0].service:OBMC_FAN_INSTANCES"
