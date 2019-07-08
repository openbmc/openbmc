SUMMARY = "An asynchronous event notification library"
HOMEPAGE = "http://libevent.org/"
BUGTRACKER = "https://github.com/libevent/libevent/issues"
SECTION = "libs"

LICENSE = "BSD & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17f20574c0b154d12236d5fbe964f549"

SRC_URI = " \
    https://github.com/libevent/libevent/releases/download/release-${PV}-stable/${BP}-stable.tar.gz \
    file://Makefile-missing-test-dir.patch \
    file://0001-test-fix-32bit-linux-regress.patch \
    file://run-ptest \
"

SRC_URI[md5sum] = "f3eeaed018542963b7d2416ef1135ecc"
SRC_URI[sha256sum] = "965cc5a8bb46ce4199a47e9b2c9e1cae3b137e8356ffdad6d94d3b9069b71dc2"

UPSTREAM_CHECK_URI = "http://libevent.org/"

S = "${WORKDIR}/${BPN}-${PV}-stable"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"

inherit autotools

# Needed for Debian packaging
LEAD_SONAME = "libevent-2.1.so"

inherit ptest multilib_header

DEPENDS = "zlib"

BBCLASSEXTEND = "native nativesdk"

do_install_append() {
        oe_multilib_header event2/event-config.h
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/test
	for file in ${B}/test/.libs/regress ${B}/test/.libs/test*
	do
		install -m 0755 $file ${D}${PTEST_PATH}/test
	done
}
