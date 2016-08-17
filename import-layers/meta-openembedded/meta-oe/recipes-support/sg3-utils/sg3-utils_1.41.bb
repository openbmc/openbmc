SUMMARY = "Utilities for working with generic SCSI devices"

DESCRIPTION = "This package contains low level utilities for devices that use the SCSI command set"

HOMEPAGE = "http://sg.danny.cz/sg/sg3_utils.html"
SECTION = "console/admin"

LICENSE = "GPLv2+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=f90da7fc52172599dbf082d7620f18ca"

SRC_URI = "http://sg.danny.cz/sg/p/sg3_utils-${PV}.tgz"

SRC_URI[md5sum] = "86ebe3881535ee5c48f81be5be44b362"
SRC_URI[sha256sum] = "c4e2893c36df1ee5b07840ab7c22129544f5dc8a55f7cc8815c9cd8e44ec31c0"

inherit autotools-brokensep

S = "${WORKDIR}/sg3_utils-${PV}"

RDEPENDS_${PN} += "bash"
