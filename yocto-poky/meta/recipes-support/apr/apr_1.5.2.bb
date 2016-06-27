SUMMARY = "Apache Portable Runtime (APR) library"
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"
DEPENDS = "util-linux"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4dfd4cd216828c8cae5de5a12f3844c8 \
                    file://include/apr_lib.h;endline=17;md5=ee42fa7575dc40580a9e01c1b75fae96"

BBCLASSEXTEND = "native nativesdk"

SRC_URI = "${APACHE_MIRROR}/apr/${BPN}-${PV}.tar.bz2 \
           file://configure_fixes.patch \
           file://cleanup.patch \
           file://configfix.patch \
           file://run-ptest \
           file://upgrade-and-fix-1.5.1.patch \
           file://Fix-packet-discards-HTTP-redirect.patch \
           file://configure.in-fix-LTFLAGS-to-make-it-work-with-ccache.patch \
"

SRC_URI[md5sum] = "4e9769f3349fe11fc0a5e1b224c236aa"
SRC_URI[sha256sum] = "7d03ed29c22a7152be45b8e50431063736df9e1daa1ddf93f6a547ba7a28f67a"

inherit autotools-brokensep lib_package binconfig multilib_header ptest

OE_BINCONFIG_EXTRA_MANGLE = " -e 's:location=source:location=installed:'"

# Added to fix some issues with cmake. Refer to https://github.com/bmwcarit/meta-ros/issues/68#issuecomment-19896928
CACHED_CONFIGUREVARS += "apr_cv_mutex_recursive=yes"

# Also suppress trying to use sctp.
#
CACHED_CONFIGUREVARS += "ac_cv_header_netinet_sctp_h=no ac_cv_header_netinet_sctp_uio_h=no"

# Otherwise libtool fails to compile apr-utils
# x86_64-linux-libtool: compile: unable to infer tagged configuration
# x86_64-linux-libtool:   error: specify a tag with '--tag'
CCACHE = ""

do_configure_prepend() {
	# Avoid absolute paths for grep since it causes failures
	# when using sstate between different hosts with different
	# install paths for grep.
	export GREP="grep"

	cd ${S}
	./buildconf
}

FILES_${PN}-dev += "${libdir}/apr.exp ${datadir}/build-1/*"
RDEPENDS_${PN}-dev += "bash"

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
	sed -i -e 's,${STAGING_DIR_HOST},,g' ${D}${datadir}/build-1/apr_rules.mk
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
