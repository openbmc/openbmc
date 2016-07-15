SUMMARY = "Tools for working with DSSSL stylesheets for SGML and XML documents"
DESCRIPTION = "OpenJade is a suite of tools for validating, \
processing, and applying DSSSL (Document Style Semantics and \
Specification Language) stylesheets to SGML and XML documents."
HOMEPAGE = "http://openjade.sourceforge.net"
SECTION = "base"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=641ff1e4511f0a87044ad42f87cb1045"

PR = "r5"

DEPENDS = "opensp-native sgml-common-native"
RDEPENDS_${PN} = "sgml-common-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/openjade/openjade-${PV}.tar.gz \
           file://makefile.patch \
           file://msggen.pl.patch \
           file://reautoconf.patch \
	   file://user-declared-default-constructor.patch \
           file://fix-regex.patch \
	   file://no-libtool.patch"

SRC_URI[md5sum] = "7df692e3186109cc00db6825b777201e"
SRC_URI[sha256sum] = "1d2d7996cc94f9b87d0c51cf0e028070ac177c4123ecbfd7ac1cb8d0b7d322d1"

UPSTREAM_CHECK_URI = "http://openjade.sourceforge.net/download.html"

inherit autotools-brokensep native

# Statically link local libs to avoid gold link issue [YOCTO #2972]
PACKAGECONFIG ?= "static-only-libs"
PACKAGECONFIG[static-only-libs] = "--enable-static --disable-shared,--enable-static --enable-shared,,"

EXTRA_OECONF = "--enable-spincludedir=${STAGING_INCDIR}/OpenSP \
                --enable-splibdir=${STAGING_LIBDIR}"

# We need to set datadir explicitly, but adding it to EXTRA_OECONF
# results in it being specified twice when configure is run.
CONFIGUREOPTS := "${@d.getVar('CONFIGUREOPTS', True).replace('--datadir=${datadir}', '--datadir=${STAGING_DATADIR}/sgml/openjade-${PV}')}"

# CONFIGUREOPTS has hard coded paths so we need to ignore it's vardeps
# there are other bits in there too but they are picked up by other variable
# dependencies so it all works out
oe_runconf[vardepsexclude] += "CONFIGUREOPTS"

CFLAGS =+ "-I${S}/include"
CXXFLAGS += "-fno-tree-dse"

SSTATEPOSTINSTFUNCS += "openjade_sstate_postinst"
SYSROOT_PREPROCESS_FUNCS += "openjade_sysroot_preprocess"
CLEANFUNCS += "openjade_sstate_clean"

# configure.in needs to be reloacted to trigger reautoconf
do_extraunpack () {
	cp ${S}/config/configure.in ${S}/
}
addtask extraunpack after do_patch before do_configure

# We need to do this else the source interdependencies aren't generated and
# build failures can result (e.g. zero size style/Makefile.dep file)
do_compile_prepend () {
	oe_runmake depend
}

do_install() {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/openjade.html
	# for details.
	install -d ${D}${bindir} ${D}${libdir}
	if ${@bb.utils.contains('PACKAGECONFIG', 'static-only-libs', 'true', 'false', d)}; then
		install -m 0755 jade/openjade ${D}${bindir}/openjade
		oe_libinstall -a -C style libostyle ${D}${libdir}
		oe_libinstall -a -C spgrove libospgrove ${D}${libdir}
		oe_libinstall -a -C grove libogrove ${D}${libdir}
	else
		install -m 0755 jade/.libs/openjade ${D}${bindir}/openjade
		oe_libinstall -a -so -C style libostyle ${D}${libdir}
		oe_libinstall -a -so -C spgrove libospgrove ${D}${libdir}
		oe_libinstall -a -so -C grove libogrove ${D}${libdir}
	fi
	ln -sf openjade ${D}${bindir}/jade

	install -d ${D}${datadir}/sgml/openjade-${PV}
	install -m 644 dsssl/catalog ${D}${datadir}/sgml/openjade-${PV}
	install -m 644 dsssl/*.dtd ${D}${datadir}/sgml/openjade-${PV}
	install -m 644 dsssl/*.dsl ${D}${datadir}/sgml/openjade-${PV}
	install -m 644 dsssl/*.sgm ${D}${datadir}/sgml/openjade-${PV}

	install -d ${datadir}/sgml/openjade-${PV}
	install -m 644 dsssl/catalog ${datadir}/sgml/openjade-${PV}/catalog

	install -d ${D}${sysconfdir}/sgml
	echo "CATALOG ${datadir}/sgml/openjade-${PV}/catalog" > \
		${D}${sysconfdir}/sgml/openjade-${PV}.cat
}

openjade_sstate_postinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
		# Ensure that the catalog file sgml-docbook.cat is properly
		# updated when the package is installed from sstate cache.
		${SYSROOT_DESTDIR}${bindir_crossscripts}/install-catalog-openjade \
			--add ${sysconfdir}/sgml/sgml-docbook.bak \
			${sysconfdir}/sgml/openjade-${PV}.cat
		${SYSROOT_DESTDIR}${bindir_crossscripts}/install-catalog-openjade \
			--add ${sysconfdir}/sgml/sgml-docbook.cat \
			${sysconfdir}/sgml/openjade-${PV}.cat
	fi
}

openjade_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 755 ${STAGING_BINDIR_NATIVE}/install-catalog ${SYSROOT_DESTDIR}${bindir_crossscripts}/install-catalog-openjade
}

openjade_sstate_clean () {
	# Ensure that the catalog file sgml-docbook.cat is properly
	# updated when the package is removed from sstate cache.
	files="${sysconfdir}/sgml/sgml-docbook.bak ${sysconfdir}/sgml/sgml-docbook.cat"
	for f in $files; do
		[ ! -f $f ] || sed -i '/\/sgml\/openjade-${PV}.cat/d' $f
	done
}
