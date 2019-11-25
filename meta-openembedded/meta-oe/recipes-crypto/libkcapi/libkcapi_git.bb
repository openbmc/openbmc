SUMMARY = "Linux Kernel Crypto API User Space Interface Library"
HOMEPAGE = "http://www.chronox.de/libkcapi.html"
LICENSE = "BSD | GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=14d5a68b28755c04ebdba226e888b157"

DEPENDS = "libtool"

S = "${WORKDIR}/git"
SRCREV = "5649050d201856bf06c8738b5d2aa1710c86ac2f"
PV = "1.1.5"
SRC_URI = " \
    git://github.com/smuellerDD/libkcapi.git \
    file://0001-kcapi-kdf-Move-code-to-fix.patch \
    file://0001-Use-__builtin_bswap32-on-Clang-if-supported.patch \
"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[testapp] = "--enable-kcapi-test,,,bash"
PACKAGECONFIG[apps] = "--enable-kcapi-speed --enable-kcapi-hasher --enable-kcapi-rngapp --enable-kcapi-encapp --enable-kcapi-dgstapp,,,"

do_install_append() {
    # bindir contains testapp and apps.  However it is always created, even
    # when no binaries are installed (empty bin_PROGRAMS in Makefile.am),
    rmdir --ignore-fail-on-non-empty ${D}${bindir}

    # Remove the generated binary checksum files
    rm -f ${D}${bindir}/.*.hmac
    rm -f ${D}${libdir}/.*.hmac
}

CPPFLAGS_append_libc-musl_toolchain-clang = " -Wno-error=sign-compare"
