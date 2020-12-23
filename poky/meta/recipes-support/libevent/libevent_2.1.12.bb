SUMMARY = "An asynchronous event notification library"
HOMEPAGE = "http://libevent.org/"
BUGTRACKER = "https://github.com/libevent/libevent/issues"
SECTION = "libs"

LICENSE = "BSD & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17f20574c0b154d12236d5fbe964f549"

SRC_URI = "https://github.com/libevent/libevent/releases/download/release-${PV}-stable/${BP}-stable.tar.gz \
           file://Makefile-missing-test-dir.patch \
           file://run-ptest \
           file://0001-test-regress_dns.c-patch-out-tests-that-require-a-wo.patch \
           "

SRC_URI[sha256sum] = "92e6de1be9ec176428fd2367677e61ceffc2ee1cb119035037a27d346b0403bb"

UPSTREAM_CHECK_URI = "http://libevent.org/"

S = "${WORKDIR}/${BPN}-${PV}-stable"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"

inherit autotools

# Needed for Debian packaging
LEAD_SONAME = "libevent-2.1.so"

inherit ptest multilib_header

DEPENDS = "zlib"

PACKAGES_DYNAMIC = "^${PN}-.*$"
python split_libevent_libs () {
    do_split_packages(d, '${libdir}', r'^libevent_([a-z]*)-.*\.so\..*', '${PN}-%s', '${SUMMARY} (%s)', prepend=True, allow_links=True)
}
PACKAGESPLITFUNCS_prepend = "split_libevent_libs "

BBCLASSEXTEND = "native nativesdk"

do_install_append() {
	rm ${D}${bindir}/event_rpcgen.py
	rmdir ${D}${bindir}
        oe_multilib_header event2/event-config.h
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/test
	for file in ${B}/test/.libs/regress ${B}/test/.libs/test*
	do
		install -m 0755 $file ${D}${PTEST_PATH}/test
	done
        
        # handle multilib
        sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
}
