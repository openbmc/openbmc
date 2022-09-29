SUMMARY = "OpenBMC org.openbmc.control.Chassis example implementation"
DESCRIPTION = "An example implementation of the org.openbmc.control.Chassis DBUS API."
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "pychassisctl"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

RDEPENDS:${PN} += "\
        python-dbus \
        python-pygobject \
        python-netclient \
        pyphosphor-dbus \
        "

FMT = "org.openbmc.control.Chassis@{0}.service"
DBUS_SERVICE:${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
