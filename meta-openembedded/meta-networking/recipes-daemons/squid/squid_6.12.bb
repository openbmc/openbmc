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

SRC_URI = "http://www.squid-cache.org/Versions/v${MAJ_VER}/${BPN}-${PV}.tar.xz \
           file://Set-up-for-cross-compilation.patch \
           file://Skip-AC_RUN_IFELSE-tests.patch \
           file://squid-use-serial-tests-config-needed-by-ptest.patch \
           file://run-ptest \
           file://volatiles.03_squid \
           file://0002-squid-make-squid-conf-tests-run-on-target-device.patch \
           file://0001-libltdl-remove-reference-to-nonexisting-directory.patch \
           file://squid.nm \
           "

SRC_URI[sha256sum] = "f3df3abb2603a513266f24a5d4699a9f0d76b9f554d1848b67f9c51cd3b3cb50"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://errors/COPYRIGHT;md5=6fbb6a2adc362e206da7b4f42846cdec \
                    "
DEPENDS = "libtool"

inherit autotools pkgconfig useradd ptest perlnative systemd

LDFLAGS:append:mipsarch = " -latomic"
LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:riscv64 = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home-dir /var/run/squid --shell /bin/false --user-group squid"

PACKAGECONFIG ??= "auth url-rewrite-helpers \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

PACKAGECONFIG[libnetfilter-conntrack] = "--with-netfilter-conntrack=${includedir}, --without-netfilter-conntrack, libnetfilter-conntrack"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[werror] = "--enable-strict-error-checking,--disable-strict-error-checking,"
PACKAGECONFIG[ssl] = "--with-openssl=yes,--with-openssl=no,openssl"
PACKAGECONFIG[auth] = "--enable-auth-basic='${BASIC_AUTH}',--disable-auth --disable-auth-basic,krb5 openldap db cyrus-sasl"
PACKAGECONFIG[url-rewrite-helpers] = "--enable-url-rewrite-helpers,--disable-url-rewrite-helpers,"
PACKAGECONFIG[systemd] = "--with-systemd,--without-systemd,systemd"

PACKAGES =+ " \
    ${PN}-conf \
    ${PN}-networkmanager \
"

BASIC_AUTH = "DB SASL LDAP"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
BASIC_AUTH += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'PAM', '', d)}"

EXTRA_OECONF += "--with-default-user=squid \
                 --sysconfdir=${sysconfdir}/${BPN} \
                 --with-logdir=${localstatedir}/log/${BPN} \
                 'PERL=${USRBINPATH}/env perl' \
                 --disable-esi \
"

# Workaround a build failure when using a native compiler that need -std=c++17
# with a cross-compiler that doesn't.
# Upstream issue closed as invalid : https://bugs.squid-cache.org/show_bug.cgi?id=5376
BUILD_CXXFLAGS += "-std=c++17"

export BUILDCXXFLAGS="${BUILD_CXXFLAGS}"

TESTDIR = "test-suite"

do_configure:prepend() {
    export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}

do_configure:append() {
   sed -i -e 's|${WORKDIR}||g' ${B}/include/autoconf.h
}

do_compile_ptest() {
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}

    # Install default config
    install -d ${D}${PTEST_PATH}/src
    install -m 0644 ${B}/src/squid.conf.default ${D}${PTEST_PATH}/src

    # autoconf.h is needed during squid-conf-tests
    install -d ${D}${PTEST_PATH}/include
    install -m 0644 ${B}/include/autoconf.h ${D}${PTEST_PATH}/include

    # do NOT need to rebuild Makefile itself
    sed -i 's/^Makefile:.*$/Makefile:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # Ensure the path for command true is correct
    sed -i 's:^TRUE = .*$:TRUE = /bin/true:' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        # Install service unit file
        install -d ${D}/${systemd_unitdir}/system
        install ${S}/tools/systemd/squid.service ${D}/${systemd_unitdir}/system
        sed -i 's:/var/run/:/run/:g' ${D}/${systemd_unitdir}/system/squid.service

        # Configure tmpfiles.d
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d ${localstatedir}/run/${BPN} 0755 squid squid -" >> ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
        echo "d ${localstatedir}/log/${BPN} 0750 squid squid -" >> ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi

    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${UNPACKDIR}/volatiles.03_squid ${D}${sysconfdir}/default/volatiles/03_squid

    rmdir "${D}${localstatedir}/run/${BPN}"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}/run"

    rmdir "${D}${localstatedir}/log/${BPN}"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}/log"

    # Install NetworkManager dispatcher reload hooks
    install -d ${D}${libdir}/NetworkManager/dispatcher.d
    install -m 0755 ${UNPACKDIR}/squid.nm ${D}${libdir}/NetworkManager/dispatcher.d/20-squid
}

SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE:${PN} = "squid.service"

FILES:${PN} += "${libdir} ${datadir}/errors ${datadir}/icons"
FILES:${PN}-dbg += "/usr/src/debug"
FILES:${PN}-doc += "${datadir}/*.txt"
FILES:${PN}-conf += "${sysconfdir}/squid"
FILES:${PN}-networkmanager = "${libdir}/NetworkManager/dispatcher.d"

RDEPENDS:${PN} += "perl ${PN}-conf"
RDEPENDS:${PN}-ptest += "perl make bash"
