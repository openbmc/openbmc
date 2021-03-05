SUMMARY = "Enca is an Extremely Naive Charset Analyser"
SECTION = "libs"
HOMEPAGE = "https://cihar.com/software/enca/"

DEPENDS += "gettext-native"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=24b9569831c46d4818450b55282476b4"

SRC_URI = "https://dl.cihar.com/enca/enca-${PV}.tar.gz \
    file://dont-run-tests.patch \
    file://makefile-remove-tools.patch \
    file://libenca-003-iconv.patch "

SRC_URI[sha256sum] = "4c305cc59f3e57f2cfc150a6ac511690f43633595760e1cb266bf23362d72f8a"

inherit autotools

do_configure_prepend() {
    # remove failing test which checks for something that isn't even used
    sed -i -e '/ye_FUNC_SCANF_MODIF_SIZE_T/d' ${S}/configure.ac
}

do_configure_append() {
    sed -i s:-I/usr/include::g ${B}/Makefile
    sed -i s:-I/usr/include::g ${B}/*/Makefile
}

do_compile() {
    cd ${S}/tools && ${BUILD_CC} -o make_hash make_hash.c
    cd ${B}
    oe_runmake
}

