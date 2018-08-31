SUMMARY = "OpenBMC org.openbmc.control.Chassis example implementation"
DESCRIPTION = "An example implementation of the org.openbmc.control.Chassis DBUS API."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "\
        python-dbus \
        python-pygobject \
        python-netclient \
        pyphosphor-dbus \
        "

SKELETON_DIR = "pychassisctl"

FMT = "org.openbmc.control.Chassis@{0}.service"
DBUS_SERVICE_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
