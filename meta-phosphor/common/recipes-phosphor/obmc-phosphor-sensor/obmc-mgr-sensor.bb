SUMMARY = "OpenBMC sensor manager"
DESCRIPTION = "OpenBMC sensor manager."
PR = "r1"

inherit skeleton-python

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-pygobject\
        pyphosphor-dbus \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pysensormgr"
