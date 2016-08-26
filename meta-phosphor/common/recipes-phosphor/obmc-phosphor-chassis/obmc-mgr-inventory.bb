SUMMARY = "OpenBMC inventory manager"
DESCRIPTION = "OpenBMC inventory manager."
PR = "r1"

inherit skeleton-python

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-argparse \
        python-dbus \
        python-json \
        python-pickle \
        python-pygobject \
        pyphosphor \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pyinventorymgr"
