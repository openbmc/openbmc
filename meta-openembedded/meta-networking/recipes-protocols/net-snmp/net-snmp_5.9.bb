SUMMARY = "Various tools relating to the Simple Network Management Protocol"
HOMEPAGE = "http://www.net-snmp.org/"
SECTION = "net"
LICENSE = "BSD & MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=9d100a395a38584f2ec18a8275261687"

DEPENDS = "openssl libnl pciutils"

SRC_URI = "${SOURCEFORGE_MIRROR}/net-snmp/net-snmp-${PV}.tar.gz \
           file://init \
           file://snmpd.conf \
           file://snmptrapd.conf \
           file://snmpd.service \
           file://snmptrapd.service \
           file://net-snmp-add-knob-whether-nlist.h-are-checked.patch \
           file://fix-libtool-finish.patch \
           file://net-snmp-testing-add-the-output-format-for-ptest.patch \
           file://run-ptest \
           file://0001-config_os_headers-Error-Fix.patch \
           file://0001-snmplib-keytools.c-Don-t-check-for-return-from-EVP_M.patch \
           file://0001-get_pid_from_inode-Include-limit.h.patch \
           file://0002-configure-fix-a-cc-check-issue.patch \
           file://0004-configure-fix-incorrect-variable.patch \
           file://net-snmp-5.7.2-fix-engineBoots-value-on-SIGHUP.patch \
           file://net-snmp-fix-for-disable-des.patch \
           file://reproducibility-have-printcap.patch \
           file://0001-ac_add_search_path.m4-keep-consistent-between-32bit.patch \
           "
SRC_URI[sha256sum] = "04303a66f85d6d8b16d3cc53bde50428877c82ab524e17591dfceaeb94df6071"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/net-snmp/files/net-snmp/"
UPSTREAM_CHECK_REGEX = "/net-snmp/(?P<pver>\d+(\.\d+)+)/"

inherit autotools-brokensep update-rc.d siteinfo systemd pkgconfig perlnative ptest multilib_script multilib_header

EXTRA_OEMAKE = "INSTALL_PREFIX=${D} OTHERLDFLAGS='${LDFLAGS}' HOST_CPPFLAGS='${BUILD_CPPFLAGS}'"

PARALLEL_MAKE = ""
CCACHE = ""

TARGET_CC_ARCH += "${LDFLAGS}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} des smux"
PACKAGECONFIG[elfutils] = "--with-elf, --without-elf, elfutils"
PACKAGECONFIG[libnl] = "--with-nl, --without-nl, libnl"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,,"

PACKAGECONFIG[perl] = "--enable-embedded-perl --with-perl-modules=yes, --disable-embedded-perl --with-perl-modules=no,\
                       perl,"
PACKAGECONFIG[des] = "--enable-des,--disable-des"
PACKAGECONFIG[smux] = ""

EXTRA_OECONF = "--enable-shared \
                --disable-manuals \
                --with-defaults \
                --with-install-prefix=${D} \
                --with-persistent-directory=${localstatedir}/lib/net-snmp \
                ${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '--with-endianness=little', '--with-endianness=big', d)} \
                --with-mib-modules='${MIB_MODULES}' \
"

MIB_MODULES = ""
MIB_MODULES_append = " ${@bb.utils.filter('PACKAGECONFIG', 'smux', d)}"

CACHED_CONFIGUREVARS = " \
    ac_cv_header_valgrind_valgrind_h=no \
    ac_cv_header_valgrind_memcheck_h=no \
    ac_cv_ETC_MNTTAB=/etc/mtab \
    lt_cv_shlibpath_overrides_runpath=yes \
    ac_cv_path_UNAMEPROG=${base_bindir}/uname \
    ac_cv_file__etc_printcap=no \
    NETSNMP_CONFIGURE_OPTIONS= \
"
export PERLPROG="${bindir}/env perl"
PERLPROG_append = "${@bb.utils.contains('PACKAGECONFIG', 'perl', ' -I${WORKDIR}', '', d)}"

HAS_PERL = "${@bb.utils.contains('PACKAGECONFIG', 'perl', '1', '0', d)}"

PTEST_BUILD_HOST_FILES += "net-snmp-config gen-variables"

do_configure_prepend() {
    sed -i -e "s|I/usr/include|I${STAGING_INCDIR}|g" \
        "${S}"/configure \
        "${S}"/configure.d/config_os_libs2

    if [ "${HAS_PERL}" = "1" ]; then
        # this may need to be changed when package perl has any change.
        cp -f ${STAGING_DIR_TARGET}/usr/lib*/perl?/*/Config.pm ${WORKDIR}/
        cp -f ${STAGING_DIR_TARGET}/usr/lib*/perl?/*/*/Config_heavy.pl ${WORKDIR}/
        sed -e "s@libpth => '/usr/lib.*@libpth => '${STAGING_DIR_TARGET}/${libdir} ${STAGING_DIR_TARGET}/${base_libdir}',@g" \
            -e "s@privlibexp => '/usr@privlibexp => '${STAGING_DIR_TARGET}/usr@g" \
            -e "s@scriptdir => '/usr@scriptdir => '${STAGING_DIR_TARGET}/usr@g" \
            -e "s@sitearchexp => '/usr@sitearchexp => '${STAGING_DIR_TARGET}/usr@g" \
            -e "s@sitelibexp => '/usr@sitearchexp => '${STAGING_DIR_TARGET}/usr@g" \
            -e "s@vendorarchexp => '/usr@vendorarchexp => '${STAGING_DIR_TARGET}/usr@g" \
            -e "s@vendorlibexp => '/usr@vendorlibexp => '${STAGING_DIR_TARGET}/usr@g" \
            -i ${WORKDIR}/Config.pm
    fi

}

do_configure_append() {
    sed -e "s@^NSC_INCLUDEDIR=.*@NSC_INCLUDEDIR=${STAGING_DIR_TARGET}\$\{includedir\}@g" \
        -e "s@^NSC_LIBDIR=-L.*@NSC_LIBDIR=-L${STAGING_DIR_TARGET}\$\{libdir\}@g" \
        -e "s@^NSC_LDFLAGS=\"-L.* @NSC_LDFLAGS=\"-L${STAGING_DIR_TARGET}\$\{libdir\} @g" \
        -i ${B}/net-snmp-config
}

do_install_append() {
    install -d ${D}${sysconfdir}/snmp
    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/snmpd
    install -m 644 ${WORKDIR}/snmpd.conf ${D}${sysconfdir}/snmp/
    install -m 644 ${WORKDIR}/snmptrapd.conf ${D}${sysconfdir}/snmp/
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/snmpd.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/snmptrapd.service ${D}${systemd_unitdir}/system
    sed    -e "s@^NSC_SRCDIR=.*@NSC_SRCDIR=.@g" \
        -i ${D}${bindir}/net-snmp-create-v3-user
    sed -e 's@^NSC_SRCDIR=.*@NSC_SRCDIR=.@g' \
        -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*--sysroot=[^ "]*@@g' \
        -e 's@[^ ]*--with-libtool-sysroot=[^ "]*@@g' \
        -e 's@[^ ]*--with-install-prefix=[^ "]*@@g' \
        -e 's@[^ ]*PKG_CONFIG_PATH=[^ "]*@@g' \
        -e 's@[^ ]*PKG_CONFIG_LIBDIR=[^ "]*@@g' \
        -e 's@${STAGING_DIR_HOST}@@g' \
        -i ${D}${bindir}/net-snmp-config

    sed -e 's@${STAGING_DIR_HOST}@@g' \
        -i ${D}${libdir}/pkgconfig/netsnmp*.pc

    sed -e "s@^NSC_INCLUDEDIR=.*@NSC_INCLUDEDIR=\$\{includedir\}@g" \
        -e "s@^NSC_LIBDIR=-L.*@NSC_LIBDIR=-L\$\{libdir\}@g" \
        -e "s@^NSC_LDFLAGS=\"-L.* @NSC_LDFLAGS=\"-L\$\{libdir\} @g" \
        -i ${D}${bindir}/net-snmp-config

    oe_multilib_header net-snmp/net-snmp-config.h
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    for i in ${S}/dist ${S}/include ${B}/include ${S}/mibs ${S}/configure \
        ${B}/net-snmp-config ${S}/testing; do
        if [ -e "$i" ]; then
            cp -R --no-dereference --preserve=mode,links -v "$i" ${D}${PTEST_PATH}
        fi
    done
    echo `autoconf -V|awk '/autoconf/{print $NF}'` > ${D}${PTEST_PATH}/dist/autoconf-version

    rmdlist="${D}${PTEST_PATH}/dist/net-snmp-solaris-build"
    for i in $rmdlist; do
        if [ -d "$i" ]; then
            rm -rf "$i"
        fi
    done
}

SYSROOT_PREPROCESS_FUNCS += "net_snmp_sysroot_preprocess"
SNMP_DBGDIR = "/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"

net_snmp_sysroot_preprocess () {
    if [ -e ${D}${bindir}/net-snmp-config ]; then
        install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
        install -m 755 ${D}${bindir}/net-snmp-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/
        sed -e "s@-I/usr/include@-I${STAGING_INCDIR}@g" \
            -e "s@^prefix=.*@prefix=${STAGING_DIR_HOST}${prefix}@g" \
            -e "s@^exec_prefix=.*@exec_prefix=${STAGING_EXECPREFIXDIR}@g" \
            -e "s@^includedir=.*@includedir=${STAGING_INCDIR}@g" \
            -e "s@^libdir=.*@libdir=${STAGING_LIBDIR}@g" \
            -e "s@^NSC_SRCDIR=.*@NSC_SRCDIR=${S}@g" \
            -e "s@-fdebug-prefix-map=${SNMP_DBGDIR}@-fdebug-prefix-map=${WORKDIR}=${SNMP_DBGDIR}@g" \
            -e "s@-fdebug-prefix-map= -fdebug-prefix-map=@-fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
                  -fdebug-prefix-map=${STAGING_DIR_HOST}=@g" \
            -e "s@--sysroot=@--sysroot=${STAGING_DIR_HOST}@g" \
            -e "s@--with-libtool-sysroot=@--with-libtool-sysroot=${STAGING_DIR_HOST}@g" \
            -e "s@--with-install-prefix=@--with-install-prefix=${D}@g" \
          -i  ${SYSROOT_DESTDIR}${bindir_crossscripts}/net-snmp-config
    fi
}

PACKAGES += "${PN}-libs ${PN}-mibs ${PN}-server ${PN}-client \
             ${PN}-server-snmpd ${PN}-server-snmptrapd \
             ${PN}-lib-netsnmp ${PN}-lib-agent ${PN}-lib-helpers \
             ${PN}-lib-mibs ${PN}-lib-trapd"

# perl module
PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'perl', '${PN}-perl-modules', '', d)}"

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-server = "1"
ALLOW_EMPTY_${PN}-libs = "1"

FILES_${PN}-perl-modules = "${libdir}/perl?/*"
RDEPENDS_${PN}-perl-modules = "perl"

FILES_${PN}-libs = ""
FILES_${PN}-mibs = "${datadir}/snmp/mibs"
FILES_${PN}-server-snmpd = "${sbindir}/snmpd \
                            ${sysconfdir}/snmp/snmpd.conf \
                            ${sysconfdir}/init.d \
                            ${systemd_unitdir}/system/snmpd.service \
"

FILES_${PN}-server-snmptrapd = "${sbindir}/snmptrapd \
                                ${sysconfdir}/snmp/snmptrapd.conf \
                                ${systemd_unitdir}/system/snmptrapd.service \
"

FILES_${PN}-lib-netsnmp = "${libdir}/libnetsnmp${SOLIBS}"
FILES_${PN}-lib-agent = "${libdir}/libnetsnmpagent${SOLIBS}"
FILES_${PN}-lib-helpers = "${libdir}/libnetsnmphelpers${SOLIBS}"
FILES_${PN}-lib-mibs = "${libdir}/libnetsnmpmibs${SOLIBS}"
FILES_${PN}-lib-trapd = "${libdir}/libnetsnmptrapd${SOLIBS}"

FILES_${PN} = ""
FILES_${PN}-client = "${bindir}/* ${datadir}/snmp/"
FILES_${PN}-dbg += "${libdir}/.debug/ ${sbindir}/.debug/ ${bindir}/.debug/"
FILES_${PN}-dev += "${bindir}/mib2c \
                    ${bindir}/mib2c-update \
                    ${bindir}/net-snmp-config \
                    ${bindir}/net-snmp-create-v3-user \
"

CONFFILES_${PN}-server-snmpd = "${sysconfdir}/snmp/snmpd.conf"
CONFFILES_${PN}-server-snmptrapd = "${sysconfdir}/snmp/snmptrapd.conf"

INITSCRIPT_PACKAGES = "${PN}-server-snmpd"
INITSCRIPT_NAME_${PN}-server-snmpd = "snmpd"
INITSCRIPT_PARAMS_${PN}-server-snmpd = "start 90 2 3 4 5 . stop 60 0 1 6 ."

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemd', '--without-systemd', d)}"

SYSTEMD_PACKAGES = "${PN}-server-snmpd \
                    ${PN}-server-snmptrapd"

SYSTEMD_SERVICE_${PN}-server-snmpd = "snmpd.service"
SYSTEMD_SERVICE_${PN}-server-snmptrapd =  "snmptrapd.service"

RDEPENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'perl', 'net-snmp-perl-modules', '', d)}"
RDEPENDS_${PN} += "net-snmp-client"
RDEPENDS_${PN}-server-snmpd += "net-snmp-mibs"
RDEPENDS_${PN}-server-snmptrapd += "net-snmp-server-snmpd ${PN}-lib-trapd"
RDEPENDS_${PN}-server += "net-snmp-server-snmpd net-snmp-server-snmptrapd"
RDEPENDS_${PN}-client += "net-snmp-mibs net-snmp-libs"
RDEPENDS_${PN}-libs += "libpci \
                        ${PN}-lib-netsnmp \
                        ${PN}-lib-agent \
                        ${PN}-lib-helpers \
                        ${PN}-lib-mibs \
"
RDEPENDS_${PN}-ptest += "perl \
                         perl-module-test \
                         perl-module-file-basename \
                         perl-module-getopt-long \
                         perl-module-file-temp \
                         perl-module-data-dumper \
"
RDEPENDS_${PN}-dev = "net-snmp-client (= ${EXTENDPKGV}) net-snmp-server (= ${EXTENDPKGV})"
RRECOMMENDS_${PN}-dbg = "net-snmp-client (= ${EXTENDPKGV}) net-snmp-server (= ${EXTENDPKGV})"

RPROVIDES_${PN}-server-snmpd += "${PN}-server-snmpd-systemd"
RREPLACES_${PN}-server-snmpd += "${PN}-server-snmpd-systemd"
RCONFLICTS_${PN}-server-snmpd += "${PN}-server-snmpd-systemd"

RPROVIDES_${PN}-server-snmptrapd += "${PN}-server-snmptrapd-systemd"
RREPLACES_${PN}-server-snmptrapd += "${PN}-server-snmptrapd-systemd"
RCONFLICTS_${PN}-server-snmptrapd += "${PN}-server-snmptrapd-systemd"

LEAD_SONAME = "libnetsnmp.so"

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/net-snmp-config"
