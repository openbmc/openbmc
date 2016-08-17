DESCRIPTION = "lib-curses provides an interface between Perl programs and \
the curses library."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;beginline=26;endline=30;md5=0b37356c5e9e28080a3422d82af8af09"

DEPENDS += "perl ncurses "

SRC_URI = "http://www.cpan.org/authors/id/G/GI/GIRAFFED/Curses-${PV}.tar.gz"

SRC_URI[md5sum] = "874c2103cc53552a0faa371c4d9119f6"
SRC_URI[sha256sum] = "808e44d5946be265af5ff0b90f3d0802108e7d1b39b0fe68a4a446fe284d322b"

S = "${WORKDIR}/Curses-${PV}"

EXTRA_CPANFLAGS = "INC=-I${STAGING_INCDIR} LIBS=-L${STAGING_LIBDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}

