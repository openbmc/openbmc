SUMMARY = "Apache Portable Runtime (APR) library"

DESCRIPTION = "Create and maintain software libraries that provide a predictable \
and consistent interface to underlying platform-specific implementations."

HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"
DEPENDS = "util-linux"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4dfd4cd216828c8cae5de5a12f3844c8 \
                    file://include/apr_lib.h;endline=15;md5=823b3d1a7225df8f7b68a69c3c2b4c71"

BBCLASSEXTEND = "native nativesdk"

SRC_URI = "${APACHE_MIRROR}/apr/${BPN}-${PV}.tar.bz2 \
           file://run-ptest \
           file://0002-apr-Remove-workdir-path-references-from-installed-ap.patch \
           file://0003-Makefile.in-configure.in-support-cross-compiling.patch \
           file://0004-Fix-packet-discards-HTTP-redirect.patch \
           file://0005-configure.in-fix-LTFLAGS-to-make-it-work-with-ccache.patch \
           file://0006-apr-fix-off_t-size-doesn-t-match-in-glibc-when-cross.patch \
           file://0007-explicitly-link-libapr-against-phtread-to-make-gold-.patch \
           file://libtoolize_check.patch \
           file://0001-Add-option-to-disable-timed-dependant-tests.patch \
           file://autoconf270.patch \
           "

SRC_URI[md5sum] = "7a14a83d664e87599ea25ff4432e48a7"
SRC_URI[sha256sum] = "e2e148f0b2e99b8e5c6caa09f6d4fb4dd3e83f744aa72a952f94f5a14436f7ea"

inherit autotools-brokensep lib_package binconfig multilib_header ptest multilib_script

OE_BINCONFIG_EXTRA_MANGLE = " -e 's:location=source:location=installed:'"

# Added to fix some issues with cmake. Refer to https://github.com/bmwcarit/meta-ros/issues/68#issuecomment-19896928
CACHED_CONFIGUREVARS += "apr_cv_mutex_recursive=yes"

# Also suppress trying to use sctp.
#
CACHED_CONFIGUREVARS += "ac_cv_header_netinet_sctp_h=no ac_cv_header_netinet_sctp_uio_h=no"

CACHED_CONFIGUREVARS += "ac_cv_sizeof_struct_iovec=yes"
CACHED_CONFIGUREVARS += "ac_cv_file__dev_zero=yes"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[timed-tests] = "--enable-timed-tests,--disable-timed-tests,"

do_configure_prepend() {
	# Avoid absolute paths for grep since it causes failures
	# when using sstate between different hosts with different
	# install paths for grep.
	export GREP="grep"

	cd ${S}
	# The "2" means libtool version 2.
	./buildconf 2
}

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/apr-1-config \
                    ${PN}-dev:${datadir}/build-1/apr_rules.mk"

FILES_${PN}-dev += "${libdir}/apr.exp ${datadir}/build-1/*"
RDEPENDS_${PN}-dev += "bash"

RDEPENDS_${PN}-ptest += "libgcc"

#for some reason, build/libtool.m4 handled by buildconf still be overwritten
#when autoconf, so handle it again.
do_configure_append() {
	sed -i -e 's/LIBTOOL=\(.*\)top_build/LIBTOOL=\1apr_build/' ${S}/build/libtool.m4
	sed -i -e 's/LIBTOOL=\(.*\)top_build/LIBTOOL=\1apr_build/' ${S}/build/apr_rules.mk
}

do_install_append() {
	oe_multilib_header apr.h
	install -d ${D}${datadir}/apr
}

do_install_append_class-target() {
	sed -i -e 's,${DEBUG_PREFIX_MAP},,g' \
	       -e 's,${STAGING_DIR_HOST},,g' ${D}${datadir}/build-1/apr_rules.mk
	sed -i -e 's,${STAGING_DIR_HOST},,g' \
	       -e 's,APR_SOURCE_DIR=.*,APR_SOURCE_DIR=,g' \
	       -e 's,APR_BUILD_DIR=.*,APR_BUILD_DIR=,g' ${D}${bindir}/apr-1-config
}

SSTATE_SCAN_FILES += "apr_rules.mk libtool"

SYSROOT_PREPROCESS_FUNCS += "apr_sysroot_preprocess"

apr_sysroot_preprocess () {
	d=${SYSROOT_DESTDIR}${datadir}/apr
	install -d $d/
	cp ${S}/build/apr_rules.mk $d/
	sed -i s,apr_builddir=.*,apr_builddir=,g $d/apr_rules.mk
	sed -i s,apr_builders=.*,apr_builders=,g $d/apr_rules.mk
	sed -i s,LIBTOOL=.*,LIBTOOL=${HOST_SYS}-libtool,g $d/apr_rules.mk
	sed -i s,\$\(apr_builders\),${STAGING_DATADIR}/apr/,g $d/apr_rules.mk
	cp ${S}/build/mkdir.sh $d/
	cp ${S}/build/make_exports.awk $d/
	cp ${S}/build/make_var_export.awk $d/
	cp ${S}/${HOST_SYS}-libtool ${SYSROOT_DESTDIR}${datadir}/build-1/libtool
}

do_compile_ptest() {
	cd ${S}/test
	oe_runmake
}

do_install_ptest() {
	t=${D}${PTEST_PATH}/test
	mkdir -p $t/.libs
	cp -r ${S}/test/data $t/
	cp -r ${S}/test/.libs/*.so $t/.libs/
	cp ${S}/test/proc_child $t/
	cp ${S}/test/readchild $t/
	cp ${S}/test/sockchild $t/
	cp ${S}/test/sockperf $t/
	cp ${S}/test/testall $t/
	cp ${S}/test/tryread $t/
}

export CONFIG_SHELL="/bin/bash"
