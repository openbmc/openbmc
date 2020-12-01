SUMMARY = "IPMI (Intelligent Platform Management Interface) library and tools"
DESCRIPTION = "OpenIPMI is an effort to create a full-function IPMI system, \
to allow full access to all IPMI information on a server \
and to abstract it to a level that will make it easy to use"

HOMEPAGE = "http://openipmi.sourceforge.net"

DEPENDS = " \
    glib-2.0 \
    ncurses \
    net-snmp \
    openssl \
    popt \
    python3 \
    swig-native \
    readline \
    "

LICENSE = "GPLv2 & LGPLv2.1 & BSD"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.BSD;md5=4b318d4160eb69c8ee53452feb1b4cdf \
                    "

SRC_URI = "${SOURCEFORGE_MIRROR}/openipmi/OpenIPMI-${PV}.tar.gz \
           file://fix-symlink-install-error-in-cmdlang.patch \
           file://openipmi-no-openipmigui-man.patch \
           file://openipmi-remove-host-path-from-la_LDFLAGS.patch \
           file://ipmi-init-fix-the-arguments.patch \
           file://include_sys_types.patch \
           file://openipmi-helper \
           file://ipmi.service \
           "

S = "${WORKDIR}/OpenIPMI-${PV}"

SRC_URI[md5sum] = "46b452e95d69c92e4172b3673ed88d52"
SRC_URI[sha256sum] = "2244124579afb14e569f34393e9ac61e658a28b6ffa8e5c0d2c1c12a8ce695cd"

inherit autotools-brokensep pkgconfig python3native perlnative update-rc.d systemd cpan-base

EXTRA_OECONF = "--disable-static \
                --with-perl='${STAGING_BINDIR_NATIVE}/perl-native/perl' \
                --with-python='${STAGING_BINDIR_NATIVE}/python3-native/python3' \
                --with-pythoninstall='${PYTHON_SITEPACKAGES_DIR}' \
                --with-glibver=2.0"

PACKAGECONFIG ??= "gdbm"
PACKAGECONFIG[gdbm] = "ac_cv_header_gdbm_h=yes,ac_cv_header_gdbm_h=no,gdbm,"

PACKAGES += "${PN}-perl ${PN}-python"
PRIVATE_LIBS_${PN}-perl = "libOpenIPMI.so.0"

FILES_${PN}-perl = " \
    ${libdir}/perl/vendor_perl/*/OpenIPMI.pm \
    ${libdir}/perl/vendor_perl/*/auto/OpenIPMI/OpenIPMI.so \
    "

FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

FILES_${PN}-dbg += " \
    ${libdir}/perl/vendor_perl/*/auto/OpenIPMI/.debug \
    ${PYTHON_SITEPACKAGES_DIR}/.debug \
    "

do_configure () {

    # Let's perform regular configuration first then handle perl issues.
    autotools_do_configure

    perl_ver=`perl -V:version | cut -d\' -f 2`
    
    # Force openipmi perl bindings to be compiled using perl-native instead of
    # the host's perl. Set the proper install directory for the resulting
    # openipmi.pm and openipmi.so
    for i in ${S}/swig/Makefile ${S}/swig/perl/Makefile; do
        echo "SAL: i = $i"
        echo "SAL: STAGING_INCDIR_NATIVE = $STAGING_INCDIR_NATIVE"
        echo "SAL: libdir = $libdir"
        sed -i -e "/^PERL_CFLAGS/s:-I/usr/local/include:-I${STAGING_INCDIR_NATIVE}:g" $i
        sed -i -e "/^PERL_CFLAGS/s:-I .* :-I ${STAGING_LIBDIR}${PERL_OWN_DIR}/perl5/${@get_perl_version(d)}/${@get_perl_arch(d)}/CORE :g" $i
        sed -i -e "/^PERL_INSTALL_DIR/s:^PERL_INSTALL_DIR = .*:PERL_INSTALL_DIR = ${libdir}/perl/vendor_perl/$perl_ver:g" $i
    done
}

do_install_append () {
    echo "SAL: D = $D"
    echo "SAL: libdir = $libdir"
    install -m 0755 -d ${D}${sysconfdir}/sysconfig ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/ipmi.init ${D}${sysconfdir}/init.d/ipmi
    install -m 0644 ${S}/ipmi.sysconf ${D}${sysconfdir}/sysconfig/ipmi
    # SAL: mv: cannot stat `/localdisk/loadbuild/slittle1/workspace/cgts_test_build/bitbake_build/tmp/work/x86_64-wrs-linux/openipmi-2.0.19-r4/image/usr/lib64/perl5': No such file or directory
    # SAL: real path to perl is /localdisk/loadbuild/slittle1/workspace/cgts_test_build/bitbake_build/tmp/work/x86_64-wrs-linux/perl-5.14.2-r8.3/package/usr/lib64/perl5 and it is a symlink to perl so no need to mv.
    if [ -d ${D}${libdir}/perl5 ]
    then
        mv ${D}${libdir}/perl5 ${D}${libdir}/perl
    fi
 
    # for systemd
    install -d ${D}${systemd_unitdir}/system
    install -m 0664 ${WORKDIR}/ipmi.service ${D}${systemd_unitdir}/system
    sed -i -e "s,@LIBEXECDIR@,${libexecdir},g" ${D}${systemd_unitdir}/system/ipmi.service
    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/openipmi-helper ${D}${libexecdir}
}

INITSCRIPT_NAME = "ipmi"
INITSCRIPT_PARAMS = "start 30 . stop 70 0 1 2 3 4 5 6 ."

SYSTEMD_SERVICE_${PN} = "ipmi.service"
SYSTEMD_AUTO_ENABLE = "disable"
