SUMMARY = "Utilities for working with generic SCSI devices"

DESCRIPTION = "This package contains low level utilities for devices that use the SCSI command set"

HOMEPAGE = "http://sg.danny.cz/sg/sg3_utils.html"
SECTION = "console/admin"

LICENSE = "GPL-2.0-or-later & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=f685699d3ac82f108aa880043fa3feb7"

SRC_URI = "http://sg.danny.cz/sg/p/sg3_utils-${PV}.tgz"
MIRRORS += "http://sg.danny.cz/sg/p https://fossies.org/linux/misc"

UPSTREAM_CHECK_REGEX = "sg3_utils-(?P<pver>\d+(\.\d+)+)\.tgz"

SRC_URI[sha256sum] = "d62b6c3cf20390fa7357044390084166d25f1d932a1135c450b69fe5c283d773"

inherit autotools-brokensep

S = "${WORKDIR}/sg3_utils-${PV}"

RDEPENDS:${PN} += "bash"
