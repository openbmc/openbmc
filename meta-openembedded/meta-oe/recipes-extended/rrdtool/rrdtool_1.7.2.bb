SUMMARY = "High performance data logging and graphing system for time series data"
HOMEPAGE = "http://oss.oetiker.ch/rrdtool/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=39df84cfd8a5e18bf988f277f7946676"

DEPENDS = "libpng zlib cairo pango glib-2.0 libxml2 groff-native python3-setuptools-native"

SRCREV = "56a83f4f52e6745cd4352f9ee008be3183a6dedf"
PV = "1.7.2"

SRC_URI = "\
    git://github.com/oetiker/rrdtool-1.x.git;branch=master;protocol=http;protocol=https \
"

S = "${WORKDIR}/git"

inherit cpan autotools-brokensep gettext python3native python3-dir systemd

BBCLASSEXTEND = "native"

SYSTEMD_PACKAGES = "rrdcached"
SYSTEMD_SERVICE_rrdcached = "rrdcached.socket rrdcached.service"

EXTRA_AUTORECONF = "-I m4 --exclude=autopoint"

PACKAGECONFIG ??= "perl ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

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
EXTRA_OEMAKE = ' PERL5LIB="${PERL_ARCHLIB}" '
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

PACKAGES =+ "${PN}-perl ${PN}-python"
PACKAGES =+ "rrdcached"

DESCRIPTION_rrdcached = \
"The rrdcached package contains the data caching daemon for RRDtool."

FILES_rrdcached = "${bindir}/rrdcached \
    ${systemd_unitdir}/system/rrdcached.service \
    ${systemd_unitdir}/system/rrdcached.socket"

FILES_${PN}-doc += "${datadir}/rrdtool/examples"

DESCRIPTION_${PN}-perl = \
"The ${PN}-perl package includes RRDtool bindings for perl."
FILES_${PN}-perl = "${libdir}/perl/vendor_perl/*/*.pm \
    ${libdir}/perl/vendor_perl/*/auto/RRDs/RRDs.*"
RDEPENDS_${PN}-perl = "perl perl-module-lib perl-module-getopt-long perl-module-time-hires \
    perl-module-io-file perl-module-ipc-open2 perl-module-io-socket"

DESCRIPTION_${PN}-python = \
"The ${PN}-python package includes RRDtool bindings for python."
FILES_${PN}-python = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
RDEPENDS_${PN}-python = "python3"

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/RRDs/.debug \
    ${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug"
