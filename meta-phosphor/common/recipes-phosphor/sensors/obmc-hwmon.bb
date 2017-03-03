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
        pyphosphor-dbus \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pyhwmon"
TMPL = "mapper-wait@.service"
TGT = "obmc-fans-ready.target"
INSTFMT = "mapper-wait@-org-openbmc-sensors-speed-fan{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_FAN_INSTANCES')}"
