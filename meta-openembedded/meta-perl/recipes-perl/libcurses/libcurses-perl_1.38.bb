DESCRIPTION = "lib-curses provides an interface between Perl programs and \
the curses library."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=26;endline=30;md5=0b37356c5e9e28080a3422d82af8af09"

DEPENDS += "perl ncurses "

SRC_URI = "http://www.cpan.org/authors/id/G/GI/GIRAFFED/Curses-${PV}.tar.gz"

SRC_URI[sha256sum] = "d521408298eb6413b209ef29d4ffcba6f5f58ee1abc60160739a17aafcb8f2f2"

S = "${WORKDIR}/Curses-${PV}"

EXTRA_CPANFLAGS = "INC=-I${STAGING_INCDIR} LIBS=-L${STAGING_LIBDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}

