SUMMARY = "Enca is an Extremely Naive Charset Analyser"
SECTION = "libs"
HOMEPAGE = "https://cihar.com/software/enca/"

DEPENDS += "gettext-native autoconf-archive-native"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=24b9569831c46d4818450b55282476b4"

SRC_URI = "https://dl.cihar.com/enca/enca-${PV}.tar.gz \
    file://cross.patch \
    file://dont-run-tests.patch \
    file://libenca-003-iconv.patch \
    file://0001-Do-not-use-MKTEMP_PROG-in-cross-build.patch \
    "
SRC_URI[sha256sum] = "4c305cc59f3e57f2cfc150a6ac511690f43633595760e1cb266bf23362d72f8a"

inherit autotools

EXTRA_OECONF += "MKTEMP_PROG=mktemp"
