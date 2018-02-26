SUMMARY = "libtar, tar manipulating library"
DESCRIPTION = "libtar is a library for manipulating POSIX tar files"
HOMEPAGE = "http://www.feep.net/libtar"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=61cbac6719ae682ce6cd45b5c11e21af"

SRC_URI = "${DEBIAN_MIRROR}/main/libt/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://fix_libtool_sysroot.patch \
           file://0002-Do-not-strip-libtar.patch \
           "

S = "${WORKDIR}/${BPN}"

SRC_URI[md5sum] = "6ced95ab3a4b33fbfe2dfb231d156cdb"
SRC_URI[sha256sum] = "50f24c857a7ef1cb092e6508758b86d06f1188508f897f3e6b40c573e8879109"

inherit autotools-brokensep

PACKAGECONFIG ??= "zlib"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"
