SUMMARY = "GNU unit testing framework, written in Expect and Tcl"
DESCRIPTION = "DejaGnu is a framework for testing other programs. Its purpose \
is to provide a single front end for all tests."
HOMEPAGE = "https://www.gnu.org/software/dejagnu/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "devel"

DEPENDS += "expect-native"
RDEPENDS:${PN} = "expect"

inherit autotools

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "87daefacd7958b4a69f88c6856dbd1634261963c414079d0c371f589cd66a2e3"

BBCLASSEXTEND = "native"
