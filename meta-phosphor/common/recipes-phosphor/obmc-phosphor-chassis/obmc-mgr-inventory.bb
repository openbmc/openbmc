SUMMARY = "OpenBMC inventory manager"
DESCRIPTION = "OpenBMC inventory manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-pickle \
        python-pygobject \
        pyphosphor \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pyinventorymgr"
DBUS_SERVICE_${PN} += "org.openbmc.Inventory.service"
SYSTEMD_SERVICE_${PN} += "obmc-sync-inventory-item@.service"
