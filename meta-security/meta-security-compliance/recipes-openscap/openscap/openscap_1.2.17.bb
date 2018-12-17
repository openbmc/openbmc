# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "NIST Certified SCAP 1.2 toolkit"
HOME_URL = "https://www.open-scap.org/tools/openscap-base/"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
LICENSE = "LGPL-2.1"

DEPENDS = "autoconf-archive pkgconfig gconf procps curl libxml2 rpm \
          libxslt libcap swig swig-native"

DEPENDS_class-native = "autoconf-archive-native pkgconfig-native swig-native curl-native libxml2-native libxslt-native dpkg-native libgcrypt-native nss-native"

SRCREV = "59c234b3e9907480c89dfbd1b466a6bf72a2d2ed"
SRC_URI = "git://github.com/akuster/openscap.git;branch=oe \
           file://crypto_pkgconfig.patch \
           file://run-ptest \
"

inherit autotools-brokensep pkgconfig python3native perlnative ptest

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "nss3 pcre rpm"
PACKAGECONFIG[pcre] = ",--enable-regex-posix, libpcre"
PACKAGECONFIG[gcrypt] = "--with-crypto=gcrypt,, libgcrypt "
PACKAGECONFIG[nss3] = "--with-crypto=nss3,, nss"
PACKAGECONFIG[python] = "--enable-python, --disable-python, python, python"
PACKAGECONFIG[python3] = "--enable-python3, --disable-python3, python3, python3"
PACKAGECONFIG[perl] = "--enable-perl, --disable-perl, perl, perl"
PACKAGECONFIG[rpm] = " --enable-util-scap-as-rpm, --disable-util-scap-as-rpm, rpm, rpm"

export LDFLAGS += " -ldl"

EXTRA_OECONF += "--enable-probes-independent --enable-probes-linux \
		--enable-probes-solaris --enable-probes-unix  --disable-util-oscap-docker\
		--enable-util-oscap-ssh --enable-util-oscap --enable-ssp --enable-sce \
"

EXTRA_OECONF_class-native += "--disable-probes-independent --enable-probes-linux \
		--disable-probes-solaris --disable-probes-unix \
		--enable-util-oscap \
"

do_configure_prepend () {
	sed -i 's:-I/usr/include:-I${STAGING_INCDIR}:' ${S}/swig/perl/Makefile.am
	sed -i 's:-I/usr/include:-I${STAGING_INCDIR}:' ${S}/swig/python3/Makefile.am
	sed -i 's:-I/usr/include:-I${STAGING_INCDIR}:' ${S}/swig/python2/Makefile.am
	sed -i 's:python2:python:' ${S}/utils/scap-as-rpm
}


include openscap.inc

do_configure_append_class-native () {
	sed -i 's:OSCAP_DEFAULT_CPE_PATH.*$:OSCAP_DEFAULT_CPE_PATH "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/cpe":' ${S}/config.h
	sed -i 's:OSCAP_DEFAULT_SCHEMA_PATH.*$:OSCAP_DEFAULT_SCHEMA_PATH "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/schemas":' ${S}/config.h
	sed -i 's:OSCAP_DEFAULT_XSLT_PATH.*$:OSCAP_DEFAULT_XSLT_PATH "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/xsl":' ${S}/config.h
}

do_clean[cleandirs] += " ${STAGING_OSCAP_BUILDDIR}"

do_install_append_class-native () {
	oscapdir=${STAGING_OSCAP_BUILDDIR}/${datadir_native}
	install -d $oscapdir	
	cp -a ${D}/${STAGING_DATADIR_NATIVE}/openscap $oscapdir
}

TESTDIR = "tests"

do_compile_ptest() {
    sed -i 's:python2:python:' ${S}/${TESTDIR}/nist/test_worker.py
    echo 'buildtest-TESTS: $(check)' >> ${TESTDIR}/Makefile
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    # install the tests
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}
}

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS_${PN} += "libxml2 python libgcc"
RDEPENDS_${PN}-ptest = "bash perl python"

BBCLASSEXTEND = "native"
