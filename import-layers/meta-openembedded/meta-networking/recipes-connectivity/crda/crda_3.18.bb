SUMMARY = "Wireless Central Regulatory Domain Agent"
HOMEPAGE = "http://wireless.kernel.org/en/developers/Regulatory/CRDA"
SECTION = "net"
LICENSE = "copyleft-next-0.3.0"
LIC_FILES_CHKSUM = "file://copyleft-next-0.3.0;md5=8743a2c359037d4d329a31e79eabeffe"

DEPENDS = "python-m2crypto-native python-native libgcrypt libnl"

SRC_URI = "https://www.kernel.org/pub/software/network/${BPN}/${BP}.tar.xz \
           file://do-not-run-ldconfig-if-destdir-is-set.patch \
           file://fix-linking-of-libraries-used-by-reglib.patch \
           file://fix-gcc-6-unused-variables.patch \
           file://0001-Makefile-respect-LDFLAGS-for-libreg.patch \
"
SRC_URI[md5sum] = "0431fef3067bf503dfb464069f06163a"
SRC_URI[sha256sum] = "43fcb9679f8b75ed87ad10944a506292def13e4afb194afa7aa921b01e8ecdbf"

inherit python-dir pythonnative
# Recursive make problem
EXTRA_OEMAKE = "MAKEFLAGS= DESTDIR=${D} LIBDIR=${libdir}/crda LDLIBREG='-Wl,-rpath,${libdir}/crda -lreg'"

do_compile() {
    oe_runmake all_noverify
}

do_install() {
    oe_runmake SBINDIR=${sbindir}/ install
}

RDEPENDS_${PN} = "udev wireless-regdb"
