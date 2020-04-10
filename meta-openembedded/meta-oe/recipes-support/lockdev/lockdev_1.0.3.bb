SUMMARY = "Locking devices library"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM="file://LICENSE;md5=d8045f3b8f929c1cb29a1e3fd737b499"

PE = "1"
SRC_URI = "http://snapshot.debian.org/archive/debian/20141023T043132Z/pool/main/l/lockdev/lockdev_${PV}.orig.tar.gz \
           http://snapshot.debian.org/archive/debian/20141023T043132Z/pool/main/l/lockdev/lockdev_${PV}-1.6.diff.gz;name=debianpatch \
           file://cross_compile.patch \
           file://build.patch \
           "
SRC_URI[md5sum] = "64b9c1b87b125fc348e892e24625524a"
SRC_URI[sha256sum] = "ccae635d7ac3fdd50897eceb250872b3d9a191d298f213e7f0c836910d869f82"
SRC_URI[debianpatch.md5sum] = "5ef6267c42fca9145e0af006ccb6aff7"
SRC_URI[debianpatch.sha256sum] = "a5405c6ee5e97e45eeb1c81330a7e9f444a58bda5e6771fa30007516c115007e"

inherit lib_package perlnative

CFLAGS += " -D__GNU_LIBRARY__"

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OEMAKE = "basedir=${D}${prefix} baselib=${baselib} LD='${CC}' LD='${CC}'"
do_compile() {
        oe_runmake shared static
}
do_install() {
        oe_runmake DESTDIR=${D} install
}
