SUMMARY = "Utilities for working with generic SCSI devices"

DESCRIPTION = "This package contains low level utilities for devices that use the SCSI command set"

HOMEPAGE = "http://sg.danny.cz/sg/sg3_utils.html"
SECTION = "console/admin"

LICENSE = "GPLv2+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=f90da7fc52172599dbf082d7620f18ca"

SRC_URI = "http://sg.danny.cz/sg/p/sg3_utils-${PV}.tgz \
"
MIRRORS += "http://sg.danny.cz/sg/p https://fossies.org/linux/misc"

UPSTREAM_CHECK_REGEX = "sg3_utils-(?P<pver>\d+(\.\d+)+)\.tgz"

SRC_URI[md5sum] = "c11d2b3ca4cc2fd01796473e5330afed"
SRC_URI[sha256sum] = "8dae684d22e71b11353a48b16c95597af90f0cbe9bbd57f98d7f5544da5cae7b"

inherit autotools-brokensep

S = "${WORKDIR}/sg3_utils-${PV}"

RDEPENDS_${PN} += "bash"
