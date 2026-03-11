SUMMARY = "library and tool to create and burn ISO-9660 images"
HOMEPAGE = "https://libburnia-project.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYRIGHT;md5=43cfe25a7e4a65f722d6253fa0649bb1"

SRC_URI = "http://files.libburnia-project.org/releases/libisoburn-${PV}.tar.gz"
SRC_URI[sha256sum] = "2b80a6f73dd633a5d243facbe97a15e5c9a07644a5e1a242c219b9375a45f71b"

DEPENDS = "libisofs libburn"
# This replaces the xorriso recipe
PROVIDES += "xorriso"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-pkg-check-modules"

do_install:append() {
    # This is a proof-of-concept UI using Tk, don't ship it
    rm -f ${D}${bindir}/xorriso-tcltk
}

PACKAGE_BEFORE_PN = "xorriso"
FILES:xorriso = "${bindir}"

BBCLASSEXTEND = "native"
