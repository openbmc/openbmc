SUMMARY = "Apache Portable Runtime (APR) companion library"
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"
DEPENDS = "apr expat gdbm"

BBCLASSEXTEND = "native nativesdk"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=519e0a18e03f7c023070568c14b077bb \
                    file://include/apu_version.h;endline=17;md5=806685a84e71f10c80144c48eb35df42"

SRC_URI = "${APACHE_MIRROR}/apr/${BPN}-${PV}.tar.gz \
           file://configfix.patch \
           file://configure_fixes.patch \
           file://run-ptest \
"

SRC_URI[md5sum] = "866825c04da827c6e5f53daff5569f42"
SRC_URI[sha256sum] = "976a12a59bc286d634a21d7be0841cc74289ea9077aa1af46be19d1a6e844c19"

EXTRA_OECONF = "--with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \ 
		--without-odbc \
		--without-pgsql \
		--with-dbm=gdbm \
		--with-gdbm=${STAGING_DIR_HOST}${prefix} \
		--without-sqlite2 \
		--with-expat=${STAGING_DIR_HOST}${prefix}"


inherit autotools lib_package binconfig

PR = "r1"

OE_BINCONFIG_EXTRA_MANGLE = " -e 's:location=source:location=installed:'"

do_configure_append() {
	if [ "${CLASSOVERRIDE}" = "class-target" ]; then
		cp ${STAGING_DATADIR}/apr/apr_rules.mk ${B}/build/rules.mk
	fi
}
do_configure_prepend_class-native() {
	mkdir ${B}/build
	cp ${STAGING_DATADIR_NATIVE}/apr/apr_rules.mk ${B}/build/rules.mk
}
do_configure_append_class-native() {
	sed -i "s#LIBTOOL=\$(SHELL) \$(apr_builddir)#LIBTOOL=\$(SHELL) ${STAGING_BINDIR_NATIVE}#" ${B}/build/rules.mk
	# sometimes there isn't SHELL
	sed -i "s#LIBTOOL=\$(apr_builddir)#LIBTOOL=${STAGING_BINDIR_NATIVE}#" ${B}/build/rules.mk
}

do_configure_prepend_class-nativesdk() {
	cp ${STAGING_DATADIR}/apr/apr_rules.mk ${S}/build/rules.mk
}

do_configure_append_class-nativesdk() {
	sed -i "s#\(apr_builddir\)=.*#\1=${STAGING_DATADIR}/build-1#" ${B}/build/rules.mk
	sed -i "s#\(apr_builders\)=.*#\1=${STAGING_DATADIR}/build-1#" ${B}/build/rules.mk
	sed -i "s#\(top_builddir\)=.*#\1=${STAGING_DATADIR}/build-1#" ${B}/build/rules.mk
	sed -i "s#\(LIBTOOL=\$(apr_builddir)\).*#\1/libtool#" ${B}/build/rules.mk
}

do_install_append_class-target() {
	sed -i -e 's,${STAGING_DIR_HOST},,g' \
	       -e 's,APU_SOURCE_DIR=.*,APR_SOURCE_DIR=,g' \
	       -e 's,APU_BUILD_DIR=.*,APR_BUILD_DIR=,g' ${D}${bindir}/apu-1-config
}

PACKAGECONFIG ??= "crypto"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[crypto] = "--with-openssl=${STAGING_DIR_HOST}${prefix} --with-crypto,--without-crypto,openssl"
PACKAGECONFIG[sqlite3] = "--with-sqlite3=${STAGING_DIR_HOST}${prefix},--without-sqlite3,sqlite3"

#files ${libdir}/apr-util-1/*.so are not symlinks but loadable modules thus they are packaged in ${PN}
FILES_${PN}     += "${libdir}/apr-util-1/apr*${SOLIBS} ${libdir}/apr-util-1/apr*${SOLIBSDEV}"
FILES_${PN}-dev += "${libdir}/aprutil.exp ${libdir}/apr-util-1/*.la"
FILES_${PN}-staticdev += "${libdir}/apr-util-1/*.a"

INSANE_SKIP_${PN} += "dev-so"

inherit ptest

RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-gconv-iso8859-1 glibc-gconv-iso8859-2 glibc-gconv-utf-7"

do_compile_ptest() {
	cd ${B}/test
	oe_runmake
}

do_install_ptest() {
	t=${D}${PTEST_PATH}/test
	mkdir $t
	for i in testall data; do \
	  cp -r ${B}/test/$i $t; \
	done
}
