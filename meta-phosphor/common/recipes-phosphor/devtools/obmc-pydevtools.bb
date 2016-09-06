SUMMARY = "OpenBMC python devtools"
DESCRIPTION = "Shortcut scripts for developers."
PR = "r1"

inherit skeleton-python

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-xml \
        python-pygobject \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
	"

SKELETON_DIR = "pytools"
