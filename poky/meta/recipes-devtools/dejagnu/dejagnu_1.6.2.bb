SUMMARY = "GNU unit testing framework, written in Expect and Tcl"
DESCRIPTION = "DejaGnu is a framework for testing other programs. Its purpose \
is to provide a single front end for all tests."
HOMEPAGE = "https://www.gnu.org/software/dejagnu/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "devel"

DEPENDS += "expect-native"

inherit autotools

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"

SRC_URI[md5sum] = "e1b07516533f351b3aba3423fafeffd6"
SRC_URI[sha256sum] = "0d0671e1b45189c5fc8ade4b3b01635fb9eeab45cf54f57db23e4c4c1a17d261"

BBCLASSEXTEND = "native"
