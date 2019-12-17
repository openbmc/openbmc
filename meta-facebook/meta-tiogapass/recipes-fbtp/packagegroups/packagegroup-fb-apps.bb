SUMMARY = "OpenBMC for Facebook - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-system = "Facebook System"
RDEPENDS_${PN}-system = " \
        entity-manager \
        dbus-sensors \
        fb-powerctrl \
        phosphor-ipmi-ipmb \
        fb-ipmi-oem \
        phosphor-pid-control \
        phosphor-hostlogger \
        phosphor-sel-logger \
        ipmitool \
        "
