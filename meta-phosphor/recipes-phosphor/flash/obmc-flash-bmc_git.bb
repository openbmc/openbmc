SUMMARY = "OpenBMC BMC update utility"
DESCRIPTION = "OpenBMC BMC update utility."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "\
        python-dbus \
        python-compression \
        python-shell \
        python-pygobject \
        python-subprocess \
        python-io \
        pyphosphor-dbus \
        "

SKELETON_DIR = "pyflashbmc"
DBUS_SERVICE_${PN} += "org.openbmc.control.BmcFlash.service"
