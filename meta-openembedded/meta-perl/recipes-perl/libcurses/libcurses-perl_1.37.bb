DESCRIPTION = "lib-curses provides an interface between Perl programs and \
the curses library."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;beginline=26;endline=30;md5=0b37356c5e9e28080a3422d82af8af09"

DEPENDS += "perl ncurses "

SRC_URI = "http://www.cpan.org/authors/id/G/GI/GIRAFFED/Curses-${PV}.tar.gz"

SRC_URI[md5sum] = "aede9d1ed9856eff89f4ce208ee59de0"
SRC_URI[sha256sum] = "74707ae3ad19b35bbefda2b1d6bd31f57b40cdac8ab872171c8714c88954db20"

S = "${WORKDIR}/Curses-${PV}"

EXTRA_CPANFLAGS = "INC=-I${STAGING_INCDIR} LIBS=-L${STAGING_LIBDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}

