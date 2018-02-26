SUMMARY = "Utilities for working with generic SCSI devices"

DESCRIPTION = "This package contains low level utilities for devices that use the SCSI command set"

HOMEPAGE = "http://sg.danny.cz/sg/sg3_utils.html"
SECTION = "console/admin"

LICENSE = "GPLv2+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=f90da7fc52172599dbf082d7620f18ca"

SRC_URI = "http://sg.danny.cz/sg/p/sg3_utils-${PV}.tgz"
MIRRORS += "http://sg.danny.cz/sg/p https://fossies.org/linux/misc"

SRC_URI[md5sum] = "28080de5bf2222f8b55a29093bec8aea"
SRC_URI[sha256sum] = "1dcb7a0309bd0ba3d4a83acb526973b80106ee26cd9f7398186cd3f0633c9ef3"

inherit autotools-brokensep

S = "${WORKDIR}/sg3_utils-${PV}"

RDEPENDS_${PN} += "bash"
