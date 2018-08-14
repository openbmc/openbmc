SUMMARY = "Locking devices library"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM="file://COPYING;md5=4fbd65380cdd255951079008b364516c"

PV = "1.0.3+git${SRCPV}"

SRCREV = "16b899645d32012cc94cc9232f64d4ddaaf0b795"
SRC_URI = "git://anonscm.debian.org/lockdev/lockdev.git"

S = "${WORKDIR}/git"

inherit lib_package autotools-brokensep

do_configure_prepend () {
    ./scripts/git-version > VERSION

    # Make automake happy
    touch ChangeLog
}

CFLAGS_append_libc-musl = " -D__GNU_LIBRARY__"
