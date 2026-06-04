SUMMARY = "OpenBMC for VR-NVL - Applications"
PR = "r1"

inherit packagegroup

PROVIDES += "virtual/obmc-system-mgmt"
RPROVIDES:${PN} += "virtual-obmc-system-mgmt"

RDEPENDS:${PN} = "entity-manager dbus-sensors nvidia-vr-nvl-udev-rules"
