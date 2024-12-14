SUMMARY = "Unbound is a validating, recursive, and caching DNS resolver"
DESCRIPTION = "Unbound's design is a set of modular components which incorporate \
	features including enhanced security (DNSSEC) validation, Internet Protocol \
	Version 6 (IPv6), and a client resolver library API as an integral part of the \
	architecture"

HOMEPAGE = "https://www.unbound.net/"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5308494bc0590c0cb036afd781d78f06"

SRC_URI = "git://github.com/NLnetLabs/unbound.git;protocol=https;branch=master \
           file://run-ptest \
           "

# 17 commits after 1.22.0 tag:
# https://github.com/NLnetLabs/unbound/compare/release-1.22.0...7985d17b57d25be262de56c29a43ae4b61c1b896
# to include fix for occasional build failure:
# https://github.com/NLnetLabs/unbound/commit/46cfbf313d812a6e50614a691e162b171dc91d7b
PV .= "+git"
SRCREV = "7985d17b57d25be262de56c29a43ae4b61c1b896"

inherit autotools pkgconfig systemd update-rc.d ptest

DEPENDS = "openssl libtool-native bison-native expat"
RDEPENDS:${PN} = "bash openssl-bin daemonize"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--with-libexpat=${STAGING_EXECPREFIXDIR} \
                --disable-rpath --with-ssl=${STAGING_EXECPREFIXDIR} \
                --enable-largefile"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[dnscrypt] = "--enable-dnscrypt, --disable-dnscrypt, libsodium"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"
PACKAGECONFIG[libevent] = "--with-libevent=${STAGING_EXECPREFIXDIR},,libevent"

do_configure:append() {
	sed -i -e 's#${RECIPE_SYSROOT}##g' ${B}/config.h
}

do_compile:append() {
        oe_runmake tests
}

do_install:append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${B}/contrib/unbound.service ${D}${systemd_unitdir}/system

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/contrib/unbound.init_yocto ${D}${sysconfdir}/init.d/unbound
}

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        install -d ${D}${PTEST_PATH}/tests/testdata

        install -m 0544 ${B}/unittest ${D}${PTEST_PATH}/tests/
        install -m 0544 ${B}/testbound ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_signatures* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_sigs* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_ds* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_nsec3_hash* ${D}${PTEST_PATH}/tests/
        install -m 0644 ${S}/testdata/*.rpl ${D}/${PTEST_PATH}/tests/testdata/
}

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

INITSCRIPT_NAME = "unbound"
INITSCRIPT_PARAMS = "defaults"
