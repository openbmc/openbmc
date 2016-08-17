SUMMARY = "The dbench (disk) and tbench (TCP) benchmarks"
SUMMARY_dbench = "Filesystem load benchmark"
SUMMARY_tbench = "TCP load benchmark"
HOMEPAGE = "http://samba.org/ftp/tridge/dbench/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "popt"

SRC_URI = "\
    http://samba.org/ftp/tridge/dbench/dbench-${PV}.tar.gz \
    file://destdir.patch \
    file://makefile.patch"

inherit autotools

PACKAGES =+ "tbench tbench-dbg"

TARGET_CC_ARCH += "${LDFLAGS}"

FILES_tbench = "${bindir}/tbench* ${prefix}/share/client.txt"
FILES_tbench-dbg += "${bindir}/.debug/tbench*"

SRC_URI[md5sum] = "1fe56ff71b9a416f8889d7150ac54da4"
SRC_URI[sha256sum] = "6001893f34e68a3cfeb5d424e1f2bfef005df96a22d86f35dc770c5bccf3aa8a"
