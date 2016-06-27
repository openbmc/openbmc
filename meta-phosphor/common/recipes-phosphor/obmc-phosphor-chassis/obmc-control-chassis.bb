SUMMARY = "OpenBMC org.openbmc.control.Chassis example implementation"
DESCRIPTION = "An example implementation of the org.openbmc.control.Chassis DBUS API."
PR = "r1"

inherit skeleton-python

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        python-netclient \
        pyphosphor \
        "

SKELETON_DIR = "pychassisctl"
