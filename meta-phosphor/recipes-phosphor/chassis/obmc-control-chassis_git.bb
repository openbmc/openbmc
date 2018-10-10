SUMMARY = "OpenBMC org.openbmc.control.Chassis example implementation"
DESCRIPTION = "An example implementation of the org.openbmc.control.Chassis DBUS API."
PR = "r1"
PV = "1.0+git${SRCPV}"

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
