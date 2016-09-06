SUMMARY = "OpenBMC state manager"
DESCRIPTION = "OpenBMC state manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-systemd

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-subprocess \
        python-pygobject \
        "

SKELETON_DIR = "pystatemgr"
SYSTEMD_SERVICE_${PN} += "obmc-mgr-state.service obmc-mgr-state.target"

TMPL = "mapper-wait@.service"
TGT = "obmc-mgr-state.target"
HOST_FMT = "../${TMPL}:${TGT}.wants/mapper-wait@-org-openbmc-settings-host{0}.service"
CHASSIS_FMT = "../${TMPL}:${TGT}.wants/mapper-wait@-org-openbmc-control-chassis{0}.service"
POWER_FMT = "../${TMPL}:${TGT}.wants/mapper-wait@-org-openbmc-control-power{0}.service"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHASSIS_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'POWER_FMT', 'OBMC_POWER_INSTANCES')}"
