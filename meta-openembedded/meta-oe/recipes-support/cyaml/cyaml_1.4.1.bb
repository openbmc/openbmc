SUMMARY = "Cyaml library"
DESCRIPTION = "LibCYAML is a library for reading and writing structured YAML documents."
HOMEPAGE = "https://github.com/tlsa/libcyaml"
SECTION = "libs"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe6f0e49348c87bddd5d27803dceaaf0"
DEPENDS = " \
    libyaml \
"
SRCREV = "07ff8654a270ec9b410acd3152b60de9e9f941af"

SRC_URI = "git://github.com/tlsa/libcyaml.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE = "'PREFIX=""' 'DESTDIR=${D}' 'CFLAGS=${CFLAGS}' 'LIBDIR=${libdir}' 'INCLUDEDIR=${includedir}' 'VARIANT=release'"

do_compile() {
    oe_runmake
}
do_install() {
    oe_runmake install
}

CFLAGS += "-pedantic -Wall -Wextra -O3 -Iinclude"