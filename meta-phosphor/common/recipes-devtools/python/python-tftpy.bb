SUMMARY = "Python is a TFTP library for the Python programming language."
DESCRIPTION = "Tftpy is a TFTP library for the Python programming language. It includes \
client and server classes, with sample implementations. Hooks are included for \
easy inclusion in a UI for populating progress indicators. It supports RFCs \
1350, 2347, 2348 and the tsize option from RFC 2349."
HOMEPAGE = "https://github.com/msoulier/tftpy"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=22770e72ae03c61f5bcc4e333b61368d"

SRC_URI = "git://github.com/msoulier/tftpy.git"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit allarch
inherit setuptools

BBCLASSEXTEND = "nativesdk"
