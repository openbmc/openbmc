SUMMARY = "Linux Kernel Crypto API User Space Interface Library"
HOMEPAGE = "http://www.chronox.de/libkcapi.html"
LICENSE = "BSD | GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d0421cf231423bda10cea691b613e866"

DEPENDS = "libtool"

S = "${WORKDIR}/git"
# Use v1.1.3 with changes on top for building in OE
SRCREV = "1c736c43eb71fbb5640d00efaf34a1edf1972c49"
PV = "1.1.3+git${SRCPV}"
SRC_URI = " \
    git://github.com/smuellerDD/libkcapi.git \
"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[testapp] = "--enable-kcapi-test,,,"
PACKAGECONFIG[apps] = "--enable-kcapi-speed --enable-kcapi-hasher --enable-kcapi-rngapp --enable-kcapi-encapp --enable-kcapi-dgstapp,,,"

do_install_append() {
    # bindir contains testapp and apps.  However it is always created, even
    # when no binaries are installed (empty bin_PROGRAMS in Makefile.am),
    rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

CPPFLAGS_append_libc-musl_toolchain-clang = " -Wno-error=sign-compare"
