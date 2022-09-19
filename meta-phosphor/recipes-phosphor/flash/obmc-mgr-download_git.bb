SUMMARY = "OpenBMC org.openbmc.managers.Download example implementation"
DESCRIPTION = "An example implementation for the org.openbmc.managers.Download DBUS API."
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "pydownloadmgr"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS:${PN} += "\
        python-dbus \
        python-pygobject \
        python-subprocess \
        pyphosphor-dbus \
        "

DBUS_SERVICE:${PN} += "org.openbmc.managers.Download.service"
