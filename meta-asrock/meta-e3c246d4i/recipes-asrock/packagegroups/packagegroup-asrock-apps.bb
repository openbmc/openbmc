SUMMARY = "OpenBMC for ASRock - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
	${PN}-flash \
	${PN}-system \
	"

PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-flash = "ASRock Flash"
RDEPENDS_${PN}-flash = " \
	obmc-control-bmc \
	phosphor-ipmi-flash \
	"

SUMMARY_${PN}-system = "ASRock System"
RDEPENDS_${PN}-system = " \
	bmcweb \
	entity-manager \
	dbus-sensors \
	webui-vue \
	phosphor-host-postd \
	phosphor-ipmi-kcs \
	phosphor-post-code-manager \
	"
