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
PACKAGES:append:fb-withhost = " \
        ${PN}-chassis \
        ${PN}-hostmgmt \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "Facebook Chassis"
RDEPENDS:remove:greatlakes:${PN}-chassis = " \
        x86-power-control \
        "

SUMMARY:${PN}-extras:tiogapass = "Extra features for tiogapass"
RDEPENDS:${PN}-extras:tiogapass = "phosphor-nvme"

SUMMARY:${PN}-fans = "Facebook Fans"
RDEPENDS:${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY:${PN}-flash = "Facebook Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-software-manager \
        "

RDEPENDS_PN_SYSTEM_EXTRAS = ""
RDEPENDS_PN_SYSTEM_EXTRAS:fb-withhost = " \
        phosphor-ipmi-ipmb \
        fb-ipmi-oem \
        phosphor-hostlogger \
        phosphor-sel-logger \
        phosphor-post-code-manager \
        phosphor-host-postd \
        phosphor-state-manager \
        "

SUMMARY:${PN}-system = "Facebook System"
RDEPENDS:${PN}-system = " \
        entity-manager \
        dbus-sensors \
        phosphor-virtual-sensor \
        phosphor-gpio-monitor-monitor \
        tzdata-core \
        fb-common-functions \
        ${RDEPENDS_PN_SYSTEM_EXTRAS} \
        "
