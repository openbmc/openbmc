SUMMARY = "OpenBMC for Alibaba - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-system \
        ${PN}-tools \
	"

PROVIDES += "virtual/obmc-system-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-system = "Alibaba System"
RDEPENDS_${PN}-system = " \
        entity-manager \
        dbus-sensors \
        phosphor-ipmi-ipmb \
        phosphor-hostlogger \
        phosphor-sel-logger \
        phosphor-post-code-manager \
        phosphor-host-postd \
        "
PROVIDES += "virtual/obmc-tools-mgmt"
RPROVIDES_${PN}-tools += "virtual-obmc-tools-mgmt"

SUMMARY_${PN}-tools = "Alibaba Tools"
RDEPENDS_${PN}-tools = " \
	ipmitool \
	"

