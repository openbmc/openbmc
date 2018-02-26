SUMMARY = "A fully-featured http proxy and web-cache daemon for Linux"
DESCRIPTION = "A fully-featured http proxy and web-cache daemon for Linux. \
Squid offers a rich access control, authorization and logging environment to \
develop web proxy and content serving applications. \
Squid offers a rich set of traffic optimization options, most of which are \
enabled by default for simpler installation and high performance. \
"
HOMEPAGE = "http://www.squid-cache.org"
SECTION = "web"
LICENSE = "GPLv2+"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 1)}"
MIN_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "http://www.squid-cache.org/Versions/v${MAJ_VER}/${MIN_VER}/${BPN}-${PV}.tar.bz2 \
           file://Set-up-for-cross-compilation.patch \
           file://Skip-AC_RUN_IFELSE-tests.patch \
           file://Fix-flawed-dynamic-ldb-link-test-in-configure.patch \
           file://squid-use-serial-tests-config-needed-by-ptest.patch \
           file://run-ptest \
           file://volatiles.03_squid \
           file://set_sysroot_patch.patch \
           file://squid-don-t-do-squid-conf-tests-at-build-time.patch \
           file://0001-SquidNew-use-noexcept-instead-of-throw-for-C-11-comp.patch \
           file://0001-configure-Check-for-Wno-error-format-truncation-comp.patch \
           file://gcc7-fixes.patch \
           file://0001-tools.cc-fixed-unused-result-warning.patch \
           "

SRC_URI_remove_toolchain-clang = "file://0001-configure-Check-for-Wno-error-format-truncation-comp.patch"

SRC_URI[md5sum] = "dc1830cd361e077814aa39bcc3691d8b"
SRC_URI[sha256sum] = "41d8845863dcd026c856508cd1599d417c8947ffd96e86e24085f9893cb8b8c2"

LIC_FILES_CHKSUM = "file://COPYING;md5=c492e2d6d32ec5c1aad0e0609a141ce9 \
                    file://errors/COPYRIGHT;md5=8861130fae91400bcf99b66f133172b3 \
                   "
DEPENDS = "libtool krb5 openldap db cyrus-sasl openssl expat libxml2"

inherit autotools pkgconfig useradd ptest perlnative

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home-dir /var/run/squid --shell /bin/false --user-group squid"

PACKAGECONFIG ??= "${@bb.utils.contains('TARGET_ARCH', 'powerpc', 'noatomics', '', d)} \
                   ${@bb.utils.contains('TARGET_ARCH', 'mips', 'noatomics', '', d)} \
                   ${@bb.utils.contains('TARGET_ARCH', 'mipsel', 'noatomics', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
                  "
PACKAGECONFIG[libnetfilter-conntrack] = "--with-netfilter-conntrack=${includedir}, --without-netfilter-conntrack, libnetfilter-conntrack"
PACKAGECONFIG[noatomics] = "squid_cv_gnu_atomics=no,squid_cv_gnu_atomics=yes,,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

BASIC_AUTH = "DB SASL LDAP"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
BASIC_AUTH += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'PAM', '', d)}"

EXTRA_OECONF += "--with-default-user=squid --enable-auth-basic='${BASIC_AUTH}' \
                 --sysconfdir=${sysconfdir}/${BPN} \
                 --with-logdir=${localstatedir}/log/${BPN} \
                 'PERL=${USRBINPATH}/env perl'"

export BUILDCXXFLAGS="${BUILD_CXXFLAGS}"

TESTDIR = "test-suite"

do_configure_prepend() {
    export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}

do_compile_ptest() {
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}

    # do NOT need to rebuild Makefile itself
    sed -i 's/^Makefile:.*$/Makefile:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # Add squid-conf-tests for runtime tests
    sed -e 's/^\(runtest-TESTS:\)/\1 squid-conf-tests/' \
        -e "s/\(list=' \$(TESTS)\)/\1 squid-conf-tests/" \
        -i ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # Ensure the path for command true is correct
    sed -i 's:^TRUE = .*$:TRUE = /bin/true:' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
}

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d ${localstatedir}/run/${BPN} 0755 squid squid -" >> ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
        echo "d ${localstatedir}/log/${BPN} 0750 squid squid -" >> ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi

    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/volatiles.03_squid ${D}${sysconfdir}/default/volatiles/03_squid

    rmdir "${D}${localstatedir}/run/${BPN}"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}/run"

    rmdir "${D}${localstatedir}/log/${BPN}"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}/log"
}

FILES_${PN} += "${libdir} ${datadir}/errors ${datadir}/icons"
FILES_${PN}-dbg += "/usr/src/debug"
FILES_${PN}-doc += "${datadir}/*.txt"

RDEPENDS_${PN} += "perl"
RDEPENDS_${PN}-ptest += "make"
