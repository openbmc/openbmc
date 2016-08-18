SUMMARY = "OpenBMC org.openbmc.control.Chassis example implementation"
DESCRIPTION = "An example implementation of the org.openbmc.control.Chassis DBUS API."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        python-netclient \
        pyphosphor \
        "

SKELETON_DIR = "pychassisctl"
BASE_BUSNAME = "org.openbmc.control.Chassis"
TEMPLATE = "${BASE_BUSNAME}@.service"

DBUS_SERVICE_${PN} += "${TEMPLATE}"
SYSTEMD_GENLINKS_${PN} += "../${TEMPLATE}:${SYSTEMD_DEFAULT_TARGET}.wants/${BASE_BUSNAME}@[0].service:OBMC_CHASSIS_INSTANCES"
