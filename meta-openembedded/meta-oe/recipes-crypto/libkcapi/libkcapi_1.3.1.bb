SUMMARY = "Linux Kernel Crypto API User Space Interface Library"
HOMEPAGE = "http://www.chronox.de/libkcapi.html"
LICENSE = "BSD-3-Clause | GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=80c467906eb826339c7f09e61808ed23"

S = "${WORKDIR}/git"
SRCREV = "2936ecd060c299157ac880650ba2c9fd94d27bb1"
SRC_URI = "git://github.com/smuellerDD/libkcapi.git;branch=master;protocol=https \
           file://0001-kcapi-kernel-if-Adjust-for-musl-msghdr-struct-compat.patch \
          "

inherit autotools

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

BBCLASSEXTEND = "native"
