SUMMARY = "An asynchronous event notification library"
DESCRIPTION = "A software library that provides asynchronous event \
notification. The libevent API provides a mechanism to execute a callback \
function when a specific event occurs on a file descriptor or after a \
timeout has been reached. libevent also supports callbacks triggered \
by signals and regular timeouts"
HOMEPAGE = "http://libevent.org/"
BUGTRACKER = "https://github.com/libevent/libevent/issues"
SECTION = "libs"

LICENSE = "BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17f20574c0b154d12236d5fbe964f549"

SRC_URI = "${GITHUB_BASE_URI}/download/release-${PV}-stable/${BP}-stable.tar.gz \
           file://Makefile-missing-test-dir.patch \
           file://run-ptest \
           file://0001-test-regress_dns.c-patch-out-tests-that-require-a-wo.patch \
           file://0002-test-regress.h-Increase-default-timeval-tolerance-50.patch \
           file://0003-test-mark-util-monotonic_prc_fallback-as-retriable.patch \
           file://0004-test-retriable-tests-are-marked-failed-only-when-all-a.patch \
           "

SRC_URI[sha256sum] = "92e6de1be9ec176428fd2367677e61ceffc2ee1cb119035037a27d346b0403bb"
UPSTREAM_CHECK_REGEX = "releases/tag/release-(?P<pver>.+)-stable"

S = "${WORKDIR}/${BPN}-${PV}-stable"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"

inherit autotools github-releases

# Needed for Debian packaging
LEAD_SONAME = "libevent-2.1.so"

inherit ptest multilib_header

DEPENDS = "zlib"

PACKAGES_DYNAMIC = "^${PN}-.*$"
python split_libevent_libs () {
    do_split_packages(d, '${libdir}', r'^libevent_([a-z]*)-.*\.so\..*', '${PN}-%s', '${SUMMARY} (%s)', prepend=True, allow_links=True)
}
PACKAGESPLITFUNCS =+ "split_libevent_libs"

BBCLASSEXTEND = "native nativesdk"

do_install:append() {
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
