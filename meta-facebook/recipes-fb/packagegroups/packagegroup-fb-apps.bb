SUMMARY = "OpenBMC for Facebook - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-extras \
        ${PN}-fans \
        ${PN}-flash \
        ${PN}-system \
        "
PACKAGES_append_fb-withhost = " \
        ${PN}-chassis \
        ${PN}-hostmgmt \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-chassis = "Facebook Chassis"
RDEPENDS_${PN}-chassis = " \
        x86-power-control \
        "

SUMMARY_${PN}-extras_tiogapass = "Extra features for tiogapass"
RDEPENDS_${PN}-extras_tiogapass = "phosphor-nvme"

SUMMARY_${PN}-fans = "Facebook Fans"
RDEPENDS_${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY_${PN}-flash = "Facebook Flash"
RDEPENDS_${PN}-flash = " \
        phosphor-software-manager \
        "

RDEPENDS_PN_SYSTEM_EXTRAS = ""
RDEPENDS_PN_SYSTEM_EXTRAS_fb-withhost = " \
        fb-powerctrl \
        phosphor-ipmi-ipmb \
        fb-ipmi-oem \
        phosphor-hostlogger \
        phosphor-sel-logger \
        ipmitool \
        phosphor-post-code-manager \
        phosphor-host-postd \
        "

SUMMARY_${PN}-system = "Facebook System"
RDEPENDS_${PN}-system = " \
        entity-manager \
        dbus-sensors \
        phosphor-virtual-sensor \
        ${RDEPENDS_PN_SYSTEM_EXTRAS} \
        "
