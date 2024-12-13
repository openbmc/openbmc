SUMMARY = "High performance data logging and graphing system for time series data"
HOMEPAGE = "http://oss.oetiker.ch/rrdtool/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=39df84cfd8a5e18bf988f277f7946676"

DEPENDS = "libpng zlib glib-2.0 libxml2 groff-native python3-setuptools-native"

SRCREV = "3af04acd38bbc61bbdcdd931dcf234c971aa5336"
PV = "1.8.0"

SRC_URI = "\
    git://github.com/oetiker/rrdtool-1.x.git;protocol=https;branch=master \
    file://b76e3c578f1e9f582e9c28f50d82b1f569602075.patch \
"

S = "${WORKDIR}/git"

inherit cpan autotools-brokensep gettext pkgconfig python3native python3-dir systemd

BBCLASSEXTEND = "native"

SYSTEMD_PACKAGES = "rrdcached"
SYSTEMD_SERVICE:rrdcached = "rrdcached.socket rrdcached.service"

EXTRA_AUTORECONF = "-I m4 --exclude=autopoint"

PACKAGECONFIG ??= "perl graph ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[python] = "--enable-python=yes \
am_cv_python_pythondir=${STAGING_LIBDIR}/python${PYTHON_BASEVERSION}/site-packages \
am_cv_python_pyexecdir=${STAGING_LIBDIR}/python${PYTHON_BASEVERSION}/site-packages,\
--disable-python,python,"

PACKAGECONFIG[perl] = \
"--enable-perl=yes --with-perl-options='INSTALLDIRS="vendor" CCFLAGS="${CFLAGS}" NO_PACKLIST=1 NO_PERLLOCAL=1' \
ac_cv_path_PERL_CC='${CC}',  \
--disable-perl,perl,"

PACKAGECONFIG[dbi] = "--enable-libdbi,--disable-libdbi,libdbi"

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir,systemd,"

PACKAGECONFIG[graph] = "--enable-rrd_graph,--disable-rrd_graph,pango cairo"

EXTRA_OECONF = " \
    --enable-shared \
    --disable-libwrap \
    --program-prefix='' \
    rd_cv_ieee_works=yes \
    --disable-ruby \
    --disable-lua \
    --disable-tcl \
    --disable-rpath \
    --enable-nls=${USE_NLS} \
    --disable-docs \
"

export STAGING_LIBDIR
export STAGING_INCDIR

# emulate cpan_do_configure
EXTRA_OEMAKE = ' CC="${CC} -Wno-incompatible-pointer-types" PERL5LIB="${PERL_ARCHLIB}" '
# Avoid do_configure error on some hosts

do_configure() {
    unset PERLHOSTLIB
    #fix the pkglib problem with newer automake
    #perl
    sed -i -e "s|-Wl,--rpath -Wl,\$rp||g" \
        ${S}/bindings/perl-shared/Makefile.PL

    #python
    sed -i -e '/PYTHON_INCLUDES="-I${/c \
    PYTHON_INCLUDES="-I=/usr/include/python${PYTHON_BASEVERSION}"' \
        ${S}/m4/acinclude.m4
    #remove the useless RPATH from the rrdtool.so
    sed -i -e 's|LD_RUN_PATH=$(libdir)||g' ${S}/bindings/Makefile.am

    autotools_do_configure

    #modify python sitepkg
    #remove the dependency of perl-shared:Makefile
    #or perl-shared/Makefile will be regenerated
    #if any code touch bindings/Makefile after below perl bindings code
    sed -i -e "s:python/setup.py install:python/setup.py install \
        --install-lib=${PYTHON_SITEPACKAGES_DIR}:" \
        -e "s:perl-shared/Makefile.PL Makefile:perl-shared/Makefile.PL:" \
        ${B}/bindings/Makefile

    #redo the perl bindings
    (
    cd ${S}/bindings/perl-shared;
    perl Makefile.PL INSTALLDIRS="vendor" INSTALLPRIVLIB="abc";

    cd ../../bindings/perl-piped;
    perl Makefile.PL INSTALLDIRS="vendor";
    )

    #change the interpreter in file
    sed -i -e "s|^PERL = ${STAGING_BINDIR_NATIVE}/.*|PERL = /usr/bin/perl|g" \
        ${B}/examples/Makefile
    sed -i -e "s|${STAGING_BINDIR_NATIVE}/perl-native/perl|/usr/bin/perl|g" \
        ${B}/examples/*.pl
}

do_install:append:class-native() {
    # Replace the shebang line in cgi-demo.cgi
    sed -i '1s|^.*$|#!/usr/bin/env rrdcgi|' ${D}${datadir}/rrdtool/examples/cgi-demo.cgi
}

PACKAGES =+ "${PN}-perl ${PN}-python"
PACKAGES =+ "rrdcached"

DESCRIPTION:rrdcached = \
"The rrdcached package contains the data caching daemon for RRDtool."

FILES:rrdcached = "${bindir}/rrdcached \
    ${systemd_unitdir}/system/rrdcached.service \
    ${systemd_unitdir}/system/rrdcached.socket"

FILES:${PN}-doc += "${datadir}/rrdtool/examples"

DESCRIPTION:${PN}-perl = \
"The ${PN}-perl package includes RRDtool bindings for perl."
FILES:${PN}-perl = "${libdir}/perl/vendor_perl/*/*.pm \
    ${libdir}/perl/vendor_perl/*/auto/RRDs/RRDs.*"
RDEPENDS:${PN}-perl = "perl perl-module-lib perl-module-getopt-long perl-module-time-hires \
    perl-module-io-file perl-module-ipc-open2 perl-module-io-socket"

DESCRIPTION:${PN}-python = \
"The ${PN}-python package includes RRDtool bindings for python."
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*"
RDEPENDS:${PN}-python = "python3"

FILES:${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/RRDs/.debug \
    ${PYTHON_SITEPACKAGES_DIR}/.debug"

# http://errors.yoctoproject.org/Errors/Details/766911/
# rrd_tune.c:239:35: error: passing argument 3 of 'optparse_init' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
