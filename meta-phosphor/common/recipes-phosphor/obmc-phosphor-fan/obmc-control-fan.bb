SUMMARY = "OpenBMC fan control"
DESCRIPTION = "OpenBMC fan control."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        pyphosphor-dbus \
        "
SKELETON_DIR = "pyfanctl"
DBUS_SERVICE_${PN} += "org.openbmc.control.Fans.service"
SYSTEMD_SERVICE_${PN} += "obmc-max-fans.service"
