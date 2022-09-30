DESCRIPTION = "lib-curses provides an interface between Perl programs and \
the curses library."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=26;endline=30;md5=0b37356c5e9e28080a3422d82af8af09"

DEPENDS += "perl ncurses "

SRC_URI = "http://www.cpan.org/authors/id/G/GI/GIRAFFED/Curses-${PV}.tar.gz \
           file://0001-testtyp.c-Use-proper-prototype-for-main-function.patch \
           "

SRC_URI[sha256sum] = "fb9efea8c7b5ed5f8ea5dee49d35252accfc05ee6e75cb9a37ab7c847cd261d7"

S = "${WORKDIR}/Curses-${PV}"

EXTRA_CPANFLAGS = "INC=-I${STAGING_INCDIR} LIBS=-L${STAGING_LIBDIR} TEST_SYMS_OPTS=-v"

# enable NCURSES_WIDECHAR=1 only if ENABLE_WIDEC has not been explicitly disabled (e.g. by the distro config).
# When compiling against the ncurses library, NCURSES_WIDECHAR needs to explicitly set to 0 in this case.
CFLAGS:append:libc-musl = "${@' -DNCURSES_WIDECHAR=1' if bb.utils.to_boolean((d.getVar('ENABLE_WIDEC') or 'True')) else ' -DNCURSES_WIDECHAR=0'} -DNCURSES_INTERNALS"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}

