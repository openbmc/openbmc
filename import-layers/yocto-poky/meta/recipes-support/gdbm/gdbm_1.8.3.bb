SUMMARY = "Key/value database library with extensible hashing"
HOMEPAGE = "http://www.gnu.org/software/gdbm/"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8e20eece214df8ef953ed5857862150"

PR = "r4"

SRC_URI = "${GNU_MIRROR}/gdbm/gdbm-${PV}.tar.gz \
	   file://makefile.patch \
           file://libtool-mode.patch \
           file://ldflags.patch"

SRC_URI[md5sum] = "1d1b1d5c0245b1c00aff92da751e9aa1"
SRC_URI[sha256sum] = "cc340338a2e28b40058ab9eb5354a21d53f88a1582ea21ba0bb185c37a281dc9"

inherit autotools texinfo

BBCLASSEXTEND = "native nativesdk"

do_install_append () {
    oe_runmake install-compat DESTDIR=${D}
    install -d ${D}${includedir}/gdbm
    install -m 0644 ${S}/dbm.h ${D}${includedir}/
    install -m 0644 ${S}/ndbm.h ${D}${includedir}/
    # Create a symlink to ndbm.h and gdbm.h in include/gdbm to let other packages to find
    # these headers
    ln -sf ../ndbm.h ${D}/${includedir}/gdbm/ndbm.h
    ln -sf ../gdbm.h ${D}/${includedir}/gdbm/gdbm.h
}
