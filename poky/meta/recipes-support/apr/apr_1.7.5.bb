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
           file://0004-Fix-packet-discards-HTTP-redirect.patch \
           file://0005-configure.in-fix-LTFLAGS-to-make-it-work-with-ccache.patch \
           file://libtoolize_check.patch \
           file://0001-Add-option-to-disable-timed-dependant-tests.patch \
           file://0001-configure-Remove-runtime-test-for-mmap-that-can-map-.patch \
           file://autoconf-2.73.patch \
           file://0001-dso-Check-for-NULL-handle-in-apr_dso_sym.patch \
           "

SRC_URI[sha256sum] = "cd0f5d52b9ab1704c72160c5ee3ed5d3d4ca2df4a7f8ab564e3cb352b67232f2"

inherit autotools-brokensep lib_package binconfig multilib_header ptest multilib_script

OE_BINCONFIG_EXTRA_MANGLE = " -e 's:location=source:location=installed:'"

# Added to fix some issues with cmake. Refer to https://github.com/bmwcarit/meta-ros/issues/68#issuecomment-19896928
CACHED_CONFIGUREVARS += "apr_cv_mutex_recursive=yes"
# Enable largefile
CACHED_CONFIGUREVARS += "apr_cv_use_lfs64=yes"
# Additional AC_TRY_RUN tests which will need to be cached for cross compile
CACHED_CONFIGUREVARS += "apr_cv_epoll=yes epoll_create1=yes apr_cv_sock_cloexec=yes \
    ac_cv_struct_rlimit=yes \
    ac_cv_func_sem_open=yes \
    apr_cv_process_shared_works=yes \
    apr_cv_mutex_robust_shared=yes \
    "
# Also suppress trying to use sctp.
#
CACHED_CONFIGUREVARS += "ac_cv_header_netinet_sctp_h=no ac_cv_header_netinet_sctp_uio_h=no"

# ac_cv_sizeof_struct_iovec is deduced using runtime check which will fail during cross-compile
CACHED_CONFIGUREVARS += "${@['ac_cv_sizeof_struct_iovec=16','ac_cv_sizeof_struct_iovec=8'][d.getVar('SITEINFO_BITS') != '32']}"

CACHED_CONFIGUREVARS += "ac_cv_file__dev_zero=yes"

CACHED_CONFIGUREVARS:append:libc-musl = " ac_cv_strerror_r_rc_int=yes"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG:append:libc-musl = " xsi-strerror"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[timed-tests] = "--enable-timed-tests,--disable-timed-tests,"
PACKAGECONFIG[xsi-strerror] = "ac_cv_strerror_r_rc_int=yes,ac_cv_strerror_r_rc_int=no,"

do_configure:prepend() {
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

FILES:${PN}-dev += "${libdir}/apr.exp ${datadir}/build-1/*"
RDEPENDS:${PN}-dev += "bash libtool"

RDEPENDS:${PN}-ptest += "libgcc"

#for some reason, build/libtool.m4 handled by buildconf still be overwritten
#when autoconf, so handle it again.
do_configure:append() {
	sed -i -e 's/LIBTOOL=\(.*\)top_build/LIBTOOL=\1apr_build/' ${S}/build/libtool.m4
	sed -i -e 's/LIBTOOL=\(.*\)top_build/LIBTOOL=\1apr_build/' ${S}/build/apr_rules.mk
}

do_install:append() {
	oe_multilib_header apr.h
	install -d ${D}${datadir}/apr
}

do_install:append:class-target() {
	rm -f ${D}${datadir}/build-1/libtool
	sed -i s,LIBTOOL=.*,LIBTOOL=libtool,g ${D}${datadir}/build-1/apr_rules.mk
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
	sed -i s,LIBTOOL=.*,LIBTOOL=libtool,g $d/apr_rules.mk
	sed -i s,\$\(apr_builders\),${STAGING_DATADIR}/apr/,g $d/apr_rules.mk
	cp ${S}/build/mkdir.sh $d/
	cp ${S}/build/make_exports.awk $d/
	cp ${S}/build/make_var_export.awk $d/
	cp ${S}/libtool ${SYSROOT_DESTDIR}${datadir}/build-1/libtool
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
