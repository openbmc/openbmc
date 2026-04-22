SUMMARY = "OpenBMC for Intel JNCRP - Applications"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit packagegroup

PACKAGES = " \
    ${PN}-chassis \
    ${PN}-system \
"

RDEPENDS:${PN} = " \
    ${PN}-chassis \
    ${PN}-system \
"

RPROVIDES:${PN}-chassis = "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-system = "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "OpenBMC Chassis applications"
RDEPENDS:${PN}-chassis = " \
    x86-power-control \
"

SUMMARY:${PN}-system = "OpenBMC system"
RDEPENDS:${PN}-system = " \
    platform-init \
"
