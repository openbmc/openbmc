SUMMARY = "A fully-featured http proxy and web-cache daemon for Linux"
DESCRIPTION = "A fully-featured http proxy and web-cache daemon for Linux. \
Squid offers a rich access control, authorization and logging environment to \
develop web proxy and content serving applications. \
Squid offers a rich set of traffic optimization options, most of which are \
enabled by default for simpler installation and high performance. \
"
HOMEPAGE = "http://www.squid-cache.org"
SECTION = "web"
LICENSE = "GPL-2.0-or-later"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 1)}"
MIN_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "http://www.squid-cache.org/Versions/v${MAJ_VER}/${BPN}-${PV}.tar.bz2 \
           file://Set-up-for-cross-compilation.patch \
           file://Skip-AC_RUN_IFELSE-tests.patch \
           file://Fix-flawed-dynamic-ldb-link-test-in-configure.patch \
           file://squid-use-serial-tests-config-needed-by-ptest.patch \
           file://run-ptest \
           file://volatiles.03_squid \
           file://set_sysroot_patch.patch \
           file://squid-don-t-do-squid-conf-tests-at-build-time.patch \
           file://0001-configure-Check-for-Wno-error-format-truncation-comp.patch \
           "

SRC_URI:remove:toolchain-clang = "file://0001-configure-Check-for-Wno-error-format-truncation-comp.patch"

SRC_URI[sha256sum] = "4c17e1eb324c4b7aa3c6889eba66eeca7ed98625d44076f7db7b027b2b093bd5"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://errors/COPYRIGHT;md5=0a7deb73d8fb7a9849af7145987829a4 \
                    "
DEPENDS = "libtool krb5 openldap db cyrus-sasl"

inherit autotools pkgconfig useradd ptest perlnative

LDFLAGS:append:mipsarch = " -latomic"
LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:riscv64 = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home-dir /var/run/squid --shell /bin/false --user-group squid"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
                  "
PACKAGECONFIG[libnetfilter-conntrack] = "--with-netfilter-conntrack=${includedir}, --without-netfilter-conntrack, libnetfilter-conntrack"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[werror] = "--enable-strict-error-checking,--disable-strict-error-checking,"
PACKAGECONFIG[esi] = "--enable-esi,--disable-esi,expat libxml2"
PACKAGECONFIG[ssl] = "--with-openssl=yes,--with-openssl=no,openssl"

BASIC_AUTH = "DB SASL LDAP"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
BASIC_AUTH += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'PAM', '', d)}"

EXTRA_OECONF += "--with-default-user=squid --enable-auth-basic='${BASIC_AUTH}' \
                 --sysconfdir=${sysconfdir}/${BPN} \
                 --with-logdir=${localstatedir}/log/${BPN} \
                 'PERL=${USRBINPATH}/env perl'"

export BUILDCXXFLAGS="${BUILD_CXXFLAGS}"

TESTDIR = "test-suite"

do_configure:prepend() {
    export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}

do_compile_ptest() {
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}

    # Needed to generate file squid.conf.default
    oe_runmake DESTDIR=${D}${PTEST_PATH} -C src install-data-local
    install -d ${D}${sysconfdir}/squid
    install -m 0644 ${D}${PTEST_PATH}/${sysconfdir}/squid/squid.conf.default ${D}${sysconfdir}/squid

    # Don't need these directories
    rm -rf ${D}${PTEST_PATH}/${sysconfdir}
    rm -rf ${D}${PTEST_PATH}/usr
    rm -rf ${D}${PTEST_PATH}/var

    # do NOT need to rebuild Makefile itself
    sed -i 's/^Makefile:.*$/Makefile:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # Add squid-conf-tests for runtime tests
    sed -e 's/^\(runtest-TESTS:\)/\1 squid-conf-tests/' \
        -i ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # Ensure the path for command true is correct
    sed -i 's:^TRUE = .*$:TRUE = /bin/true:' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
}

do_install:append() {
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

FILES:${PN} += "${libdir} ${datadir}/errors ${datadir}/icons"
FILES:${PN}-dbg += "/usr/src/debug"
FILES:${PN}-doc += "${datadir}/*.txt"
FILES:${PN}-ptest += "${sysconfdir}/squid/squid.conf.default"

RDEPENDS:${PN} += "perl"
RDEPENDS:${PN}-ptest += "perl make"
