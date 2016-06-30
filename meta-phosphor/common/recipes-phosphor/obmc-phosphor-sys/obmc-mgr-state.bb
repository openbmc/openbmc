SUMMARY = "OpenBMC state manager"
DESCRIPTION = "OpenBMC state manager."
PR = "r1"

inherit skeleton-python

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-subprocess \
        python-pygobject \
        "

SKELETON_DIR = "pystatemgr"
