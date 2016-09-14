SUMMARY = "OpenBMC inventory manager"
DESCRIPTION = "OpenBMC inventory manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RRECOMMENDS_${PN} += "virtual-obmc-inventory-data"

RDEPENDS_${PN} += "\
        python-argparse \
        python-dbus \
        python-json \
        python-pickle \
        python-pygobject \
        python-subprocess \
        pyphosphor-dbus \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pyinventorymgr"
DBUS_SERVICE_${PN} += "org.openbmc.Inventory.service"
SYSTEMD_SERVICE_${PN} += "obmc-sync-inventory-item@.service"
