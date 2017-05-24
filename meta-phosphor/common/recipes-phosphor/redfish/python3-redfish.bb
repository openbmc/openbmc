SUMMARY = "A redfish conformant REST interface"

DESCRIPTION = "A redfish server implementation"
HOMEPAGE = "https://github.com/DMTF/Redfish-Profile-Simulator/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=cee7a7694b5bf14bc9d3e0fbe78a64af"

SRC_URI = "git://github.com/DMTF/Redfish-Profile-Simulator.git;branch=RedDrum-095-upgrade"
SRCREV="202424463f4039e111a3a0e8ce1cc8256a11e028"
S="${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-flask ${PYTHON_PN}-json ${PYTHON_PN}-netclient ${PYTHON_PN}-misc"
