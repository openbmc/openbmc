SUMMARY = "OpenBMC BMC update utility"
DESCRIPTION = "OpenBMC BMC update utility."
PR = "r1"

inherit skeleton-python

RDEPENDS_${PN} += "\
        python-dbus \
        python-compression \
        python-shell \
        python-pygobject \
        pyphosphor \
        "

SKELETON_DIR = "pyflashbmc"
