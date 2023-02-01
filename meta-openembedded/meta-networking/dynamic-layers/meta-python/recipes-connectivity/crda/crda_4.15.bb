SUMMARY = "Wireless Central Regulatory Domain Agent"
HOMEPAGE = "http://wireless.kernel.org/en/developers/Regulatory/CRDA"
SECTION = "net"
LICENSE = "copyleft-next-0.3.0"
LIC_FILES_CHKSUM = "file://copyleft-next-0.3.0;md5=8743a2c359037d4d329a31e79eabeffe"

DEPENDS = "python3-m2crypto-native libnl libgcrypt"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/mcgrof/crda.git;branch=master \
           file://do-not-run-ldconfig-if-destdir-is-set.patch \
           file://fix-linking-of-libraries-used-by-reglib.patch \
           file://0001-Makefile-respect-LDFLAGS-for-libreg.patch \
           file://use-target-word-size-instead-of-host-s.patch \
           file://fix-issues-when-USE_OPENSSL-1.patch \
           file://crda-4.14-python-3.patch \
           file://0001-Make-alpha2-to-be-3-characters-long.patch \
           file://0001-reglib-Remove-unused-variables.patch \
"
SRCREV = "6aeea99ceeec85dd7a9202ee39c7f3b2a8f5195d"

S = "${WORKDIR}/git"

inherit pkgconfig python3-dir python3native siteinfo

# Recursive make problem
EXTRA_OEMAKE = "MAKEFLAGS= DESTDIR=${D} LIBDIR=${libdir}/crda LDLIBREG='-Wl,-rpath,${libdir}/crda -lreg' \
                UDEV_RULE_DIR=${nonarch_base_libdir}/udev/rules.d/"
TARGET_BITS = "${SITEINFO_BITS}"
export TARGET_BITS

do_compile() {
    oe_runmake all_noverify
}

do_install() {
    oe_runmake SBINDIR=${sbindir}/ install
}

RDEPENDS:${PN} = "udev wireless-regdb"
