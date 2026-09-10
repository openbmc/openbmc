DESCRIPTION = "\
IPC::Run allows you run and interact with child processes \
using files, pipes, and pseudo-ttys. Both system()-style and scripted \
usages are supported and may be mixed. Likewise, functional and OO API \
styles are both supported and may be mixed."
HOMEPAGE = "https://metacpan.org/release/IPC-Run"
SECTION = "libs"
LICENSE = "Artistic-1.0 OR GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0cec6aceee7559197f14caccd0f4c2f9"
DEPENDS = "perl"

SRC_URI = "git://github.com/toddr/IPC-Run.git;branch=main;protocol=https"
SRCREV = "a8232b0a3e0e713696bf6bb8f970c561d0fe3d8f"


inherit cpan

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}
