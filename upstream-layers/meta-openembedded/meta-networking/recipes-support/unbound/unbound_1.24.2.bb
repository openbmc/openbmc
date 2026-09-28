SUMMARY = "Unbound is a validating, recursive, and caching DNS resolver"
DESCRIPTION = "Unbound's design is a set of modular components which incorporate \
	features including enhanced security (DNSSEC) validation, Internet Protocol \
	Version 6 (IPv6), and a client resolver library API as an integral part of the \
	architecture"

HOMEPAGE = "https://www.unbound.net/"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5308494bc0590c0cb036afd781d78f06"

SRC_URI = "git://github.com/NLnetLabs/unbound.git;protocol=https;branch=master;tag=release-${PV} \
           file://run-ptest \
           file://CVE-2026-33278.patch \
           file://CVE-2026-42944-1.patch \
           file://CVE-2026-42944-2.patch \
           file://CVE-2026-42959.patch \
           file://CVE-2026-32792.patch \
           file://CVE-2026-40622.patch \
           file://CVE-2026-41292.patch \
           file://CVE-2026-42534.patch \
           file://CVE-2026-42923.patch \
           file://CVE-2026-42960.patch \
           file://CVE-2026-44390.patch \
           file://CVE-2026-44608.patch \
           file://CVE-2026-46582.patch \
           file://CVE-2026-32665.patch \
           file://CVE-2026-42955.patch \
           file://CVE-2026-44621.patch \
           file://CVE-2026-44687.patch \
           file://CVE-2026-50045.patch \
           file://CVE-2026-50046.patch \
           file://CVE-2026-50243.patch \
           file://CVE-2026-50248.patch \
           file://CVE-2026-50251.patch \
           file://CVE-2026-50252.patch \
           file://CVE-2026-52863.patch \
           file://CVE-2026-54478.patch \
           file://CVE-2026-55708.patch \
           file://CVE-2026-55717.patch \
           file://CVE-2026-55973.patch \
           file://CVE-2026-55990.patch \
           file://CVE-2026-55991.patch \
           file://CVE-2026-56416.patch \
           file://CVE-2026-56444.patch \
           "

SRCREV = "f6269baa605d31859f28770e01a24e3677e5f82c"

inherit autotools pkgconfig systemd update-rc.d ptest

DEPENDS = "openssl libtool-native bison-native expat"
RDEPENDS:${PN} = "bash openssl-bin daemonize"


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
        install -d ${D}${PTEST_PATH}/tests/testdata
        install -d ${D}${PTEST_PATH}/sources/${BP}
        # unittests are looking for the data in the source folder, though it's the
        # same data used by the other tests
        ln -sr ${D}${PTEST_PATH}/tests ${D}${PTEST_PATH}/sources/${BP}/testdata

        install -m 0544 ${B}/unittest ${D}${PTEST_PATH}/tests/
        install -m 0544 ${B}/testbound ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_signatures* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_sigs* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_ds* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_nsec3_hash* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/test_ldnsrr* ${D}${PTEST_PATH}/tests/
        install -m 0664 ${S}/testdata/zonemd.example* ${D}${PTEST_PATH}/tests/
        install -m 0644 ${S}/testdata/*.rpl ${D}/${PTEST_PATH}/tests/testdata/

}

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

INITSCRIPT_NAME = "unbound"
INITSCRIPT_PARAMS = "defaults"
