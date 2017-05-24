SUMMARY = "A redfish conformant REST interface"

DESCRIPTION = "A redfish server implementation"
HOMEPAGE = "https://github.com/DMTF/Redfish-Profile-Simulator/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=cee7a7694b5bf14bc9d3e0fbe78a64af"

SRC_URI = "git://github.com/DMTF/Redfish-Profile-Simulator.git;branch=RedDrum-095-upgrade"
SRCREV="e4e25869f383170ab029ef7093f73c439dfd7fda"
SRC_URI += "file://0001-obmc-connections.patch"
S="${WORKDIR}/git"

FILES_${PN} += "${datadir}/RedDrum"

inherit setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-flask ${PYTHON_PN}-json ${PYTHON_PN}-netclient ${PYTHON_PN}-misc ${PYTHON_PN}-setuptools glibc-utils localedef"
