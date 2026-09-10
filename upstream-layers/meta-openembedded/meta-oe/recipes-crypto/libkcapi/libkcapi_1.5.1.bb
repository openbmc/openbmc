SUMMARY = "Linux Kernel Crypto API User Space Interface Library"
HOMEPAGE = "https://www.chronox.de/libkcapi/index.html"
LICENSE = "BSD-3-Clause OR GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fed53d2ab12f2d813d4177dcf4792528"

SRCREV = "df40ea48ecbf1afc6d3efa5494673fa73bd40826"
SRC_URI = "git://github.com/smuellerDD/libkcapi.git;branch=master;protocol=https;tag=v${PV} \
          "

inherit autotools

CACHED_CONFIGUREVARS += "ac_cv_prog_cc_c23=no"

PACKAGECONFIG ??= ""
PACKAGECONFIG[testapp] = "--enable-kcapi-test,,,bash"
PACKAGECONFIG[apps] = "--enable-kcapi-speed --enable-kcapi-hasher --enable-kcapi-rngapp --enable-kcapi-encapp --enable-kcapi-dgstapp,,,"
PACKAGECONFIG[hasher_only] = "--enable-kcapi-hasher --disable-lib-kdf --disable-lib-sym --disable-lib-aead --disable-lib-rng,,,"

do_install:append() {
    # bindir contains testapp and apps.  However it is always created, even
    # when no binaries are installed (empty bin_PROGRAMS in Makefile.am),
    rmdir --ignore-fail-on-non-empty ${D}${bindir}

    # Remove the generated binary checksum files
    rm -f ${D}${bindir}/.*.hmac
    rm -f ${D}${libdir}/.*.hmac
}

CPPFLAGS:append:libc-musl:toolchain-clang = " -Wno-error=sign-compare"
CPPFLAGS:remove:libc-musl:toolchain-clang = "-Wno-error=sign-conversion"
CPPFLAGS:append:libc-musl = " -Wno-error=sign-conversion"

BBCLASSEXTEND = "native"
