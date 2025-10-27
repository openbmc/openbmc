SUMMARY = "OpenBMC for NVL32 - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-system = "OpenBMC System"
RDEPENDS:${PN}-system = " \
        entity-manager \
        dbus-sensors \
        x86-power-control \
        platform-init \
        phosphor-settings-manager \
        "
