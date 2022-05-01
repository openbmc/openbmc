SUMMARY = "Locking devices library"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM="file://LICENSE;md5=d8045f3b8f929c1cb29a1e3fd737b499"

PE = "1"
SRC_URI = "http://snapshot.debian.org/archive/debian/20141023T043132Z/pool/main/l/lockdev/lockdev_${PV}.orig.tar.gz \
           file://cross_compile.patch \
           file://build.patch \
           file://0001-lockdev-Define-MAJOR-MINOR-for-non-glibc-case.patch \
           "
SRC_URI[sha256sum] = "ccae635d7ac3fdd50897eceb250872b3d9a191d298f213e7f0c836910d869f82"

inherit lib_package perlnative

TARGET_CC_ARCH += "${LDFLAGS}"

CFLAGS:append:libc-musl = " -D__GNU_LIBRARY__"

EXTRA_OEMAKE = "basedir=${D}${prefix} baselib=${baselib} LD='${CC}'"

do_compile() {
        oe_runmake shared static
}
do_install() {
        oe_runmake DESTDIR=${D} install
}
