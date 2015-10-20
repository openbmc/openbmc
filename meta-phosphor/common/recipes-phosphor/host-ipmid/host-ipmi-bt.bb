SUMMARY = "Phosphor OpenBMC BT to DBUS"
DESCRIPTION = "Phosphor OpenBMC BT to DBUS."
PR = "r1"

RDEPENDS_${PN} += "python-subprocess"

SYSTEMD_SERVICE_${PN} = "host-ipmi-bt.service"

inherit obmc-phosphor-pydbus-service
inherit obmc-phosphor-host-ipmi-hw

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/skeleton.git;subpath=bin;destsuffix=git"
SRCREV="${AUTOREV}"

SCRIPT_NAME = "ipmid.py"
INSTALL_NAME = "host-ipmi-bt"
