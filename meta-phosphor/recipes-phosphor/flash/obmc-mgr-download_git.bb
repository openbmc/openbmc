SUMMARY = "OpenBMC org.openbmc.managers.Download example implementation"
DESCRIPTION = "An example implementation for the org.openbmc.managers.Download DBUS API."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        python-subprocess \
        pyphosphor-dbus \
        "

SKELETON_DIR = "pydownloadmgr"
DBUS_SERVICE_${PN} += "org.openbmc.managers.Download.service"
