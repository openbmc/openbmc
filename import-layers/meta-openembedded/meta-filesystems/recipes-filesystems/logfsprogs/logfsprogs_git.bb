SUMMARY = "LogFS Programs: used to create LogFS file system"
DESCRIPTION = "\
LogFS is a Linux log-structured and scalable flash file system, intended \
for use on large devices of flash memory. It is written by Jörn Engel and \
in part sponsored by the CE Linux Forum. \
LogFS is included in the mainline Linux kernel and was introduced in \
version 2.6.34, released on May 16, 2010."
HOMEPAGE = "https://github.com/prasad-joshi/logfsprogs"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://fsck.c;md5=3859dc73da97909ff1d0125e88a27e02"
DEPENDS = "zlib"

SRC_URI = "git://github.com/prasad-joshi/logfsprogs.git"
SRCREV = "45b72c81ce3c6fa17ca19bafc207ea93e76312f4"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CC="${CC}" LD="${LD}" AR="${AR}""

do_install () {
    mkdir -p ${D}${bindir}
    install -m 0755 ${S}/mklogfs ${D}${bindir}/mklogfs
}

BBCLASSEXTEND = "native nativesdk"
