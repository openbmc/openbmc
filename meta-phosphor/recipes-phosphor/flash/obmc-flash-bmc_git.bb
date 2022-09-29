SUMMARY = "OpenBMC BMC update utility"
DESCRIPTION = "OpenBMC BMC update utility."
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "pyflashbmc"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS:${PN} += "\
        python-dbus \
        python-compression \
        python-shell \
        python-pygobject \
        python-subprocess \
        python-io \
        pyphosphor-dbus \
        "

DBUS_SERVICE:${PN} += "org.openbmc.control.BmcFlash.service"
