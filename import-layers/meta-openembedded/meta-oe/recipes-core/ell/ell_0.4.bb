SUMMARY  = "Embedded Linux Library"
DESCRIPTION = "ELL is a DBUS library which provides DBUS bindings."
LICENSE  = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb504b67c50331fc78734fed90fb0e09"
SECTION = "libs"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
SRCREV = "b4aea06fabb2af1af01f861f8f394c75950b6d47"
SRC_URI = "git://git.kernel.org/pub/scm/libs/ell/ell.git \
           file://0001-ell-fix-build-with-musl-libc.patch \
          "

do_configure_prepend () {
    mkdir ${S}/build-aux
}

DEPENDS = "dbus"
