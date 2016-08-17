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
           file://CVE-2016-3947.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=c492e2d6d32ec5c1aad0e0609a141ce9 \
                    file://errors/COPYRIGHT;md5=0fed8f1462f6fdbc62bb431bcb618f46 \
                   "
SRC_URI[md5sum] = "06e43abc67aedcc3903a2780de20a3ed"
SRC_URI[sha256sum] = "b7dcec8c5cb7f5687aff4256a7522f670c310a350cc9e9c0f29f3fd9cf88d017"

DEPENDS = "libtool krb5 openldap db cyrus-sasl"

inherit autotools useradd ptest

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home-dir /var/run/squid --shell /bin/false --user-group squid"

PACKAGECONFIG ??= "${@base_contains('TARGET_ARCH', 'powerpc', 'noatomics', '', d)} \
                   ${@base_contains('TARGET_ARCH', 'mips', 'noatomics', '', d)} \
                  "
PACKAGECONFIG[libnetfilter-conntrack] = "--with-netfilter-conntrack=${includedir}, --without-netfilter-conntrack, libnetfilter-conntrack"
PACKAGECONFIG[noatomics] = "squid_cv_gnu_atomics=no,squid_cv_gnu_atomics=yes,,"

BASIC_AUTH = "DB SASL LDAP NIS"
DEPENDS += "${@base_contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
BASIC_AUTH += "${@base_contains('DISTRO_FEATURES', 'pam', 'PAM', '', d)}"

EXTRA_OECONF += "--with-default-user=squid --enable-auth-basic='${BASIC_AUTH}'"
export BUILDCXXFLAGS="${BUILD_CXXFLAGS}"

TESTDIR = "test-suite"
do_compile_ptest() {
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}

    # do NOT need to rebuild Makefile itself
    sed -i 's/^Makefile:.*$/Makefile:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
}

do_install_append() {
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/volatiles.03_squid  ${D}${sysconfdir}/default/volatiles/volatiles.03_squid
	rmdir "${D}${localstatedir}/run/${BPN}"
	rmdir --ignore-fail-on-non-empty "${D}${localstatedir}/run"
}

FILES_${PN} += "${libdir} ${datadir}/errors ${datadir}/icons"
FILES_${PN}-dbg += "/usr/src/debug"
FILES_${PN}-doc += "${datadir}/*.txt"

RDEPENDS_${PN} += "perl"
RDEPENDS_${PN}-ptest += "make"

# http://errors.yoctoproject.org/Errors/Details/35128/
PNBLACKLIST[squid] ?= "BROKEN: sysroots/qemuarm/usr/include/linux/in.h:28:16: error: redeclaration of 'IPPROTO_IP'"
