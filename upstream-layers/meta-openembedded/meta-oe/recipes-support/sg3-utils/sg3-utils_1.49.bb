SUMMARY = "Utilities for working with generic SCSI devices"

DESCRIPTION = "This package contains low level utilities for devices that use the SCSI command set"

HOMEPAGE = "http://sg.danny.cz/sg/sg3_utils.html"
SECTION = "console/admin"

LICENSE = "BSD-2-Clause AND GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6693c9d85dff87dd292973caef66b6a6"

SRC_URI = "http://sg.danny.cz/sg/p/sg3_utils-${PV}.tgz"
MIRRORS += "http://sg.danny.cz/sg/p https://fossies.org/linux/misc"

UPSTREAM_CHECK_REGEX = "sg3_utils-(?P<pver>\d+(\.\d+)+)\.tgz"

SRC_URI[sha256sum] = "84ba5095108dd97cfb554d7b38721f2aa2b43fc1f93cf82abd6efe4c9da220a7"

inherit autotools-brokensep

S = "${UNPACKDIR}/sg3_utils-${PV}"

RDEPENDS:${PN} += "bash"
