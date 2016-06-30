SUMMARY = "OpenBMC org.openbmc.managers.Download example implementation"
DESCRIPTION = "An example implementation for the org.openbmc.managers.Download DBUS API."
PR = "r1"

inherit skeleton-python

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        python-subprocess \
        pyphosphor \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pydownloadmgr"
