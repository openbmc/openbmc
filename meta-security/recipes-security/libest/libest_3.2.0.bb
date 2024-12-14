SUMMARY = "EST is used for secure certificate  \
enrollment and is compatible with Suite B certs (as well as RSA \
and DSA certificates)"

LICENSE = "OpenSSL"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ecb78acde8e3b795de8ef6b61aed5885"

SRCREV = "4ca02c6d7540f2b1bcea278a4fbe373daac7103b"
SRC_URI = "git://github.com/cisco/libest;branch=main;protocol=https"

DEPENDS = "openssl"

#fatal error: execinfo.h: No such file or directory
DEPENDS:append:libc-musl = " libexecinfo"

inherit autotools-brokensep

EXTRA_OECONF = "--disable-pthreads --with-ssl-dir=${STAGING_LIBDIR}"

CFLAGS += "-fcommon"
LDFLAGS:append:libc-musl = " -lexecinfo"

S = "${UNPACKDIR}/git"

PACKAGES = "${PN} ${PN}-dbg ${PN}-dev"

FILES:${PN} = "${bindir}/* ${libdir}/libest-3.2.0p.so"

# https://github.com/cisco/libest/issues/104
SKIP_RECIPE[libest] ?= "Needs porting to openssl 3.x"
