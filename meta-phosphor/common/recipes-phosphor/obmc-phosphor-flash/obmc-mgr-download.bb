SUMMARY = "OpenBMC org.openbmc.managers.Download example implementation"
DESCRIPTION = "An example implementation for the org.openbmc.managers.Download DBUS API."
PR = "r1"

inherit skeleton-python

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        python-subprocess \
        pyphosphor \
        "

SKELETON_DIR = "pydownloadmgr"
