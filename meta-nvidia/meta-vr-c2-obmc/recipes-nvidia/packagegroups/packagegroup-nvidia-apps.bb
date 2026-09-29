SUMMARY = "OpenBMC for MGX Vera C2 - Applications"
PR = "r1"

inherit packagegroup

PROVIDES += "virtual/obmc-system-mgmt"
RPROVIDES:${PN} += "virtual-obmc-system-mgmt"

RDEPENDS:${PN} = "entity-manager dbus-sensors"
