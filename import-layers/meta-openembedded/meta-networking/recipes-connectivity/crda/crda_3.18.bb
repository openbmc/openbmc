SUMMARY = "Wireless Central Regulatory Domain Agent"
HOMEPAGE = "http://wireless.kernel.org/en/developers/Regulatory/CRDA"
SECTION = "net"
LICENSE = "copyleft-next-0.3.0 & ISC"
LIC_FILES_CHKSUM = "file://copyleft-next-0.3.0;md5=8743a2c359037d4d329a31e79eabeffe \
                    file://${WORKDIR}/wireless-regdb-2014.11.18/LICENSE;md5=07c4f6dea3845b02a18dc00c8c87699c"

DEPENDS = "python-m2crypto-native python-native libgcrypt libnl"

SRC_URI = "https://www.kernel.org/pub/software/network/crda/${BP}.tar.xz;name=crda \
           https://www.kernel.org/pub/software/network/wireless-regdb/wireless-regdb-2014.11.18.tar.xz;name=bin \
           file://do-not-run-ldconfig-if-destdir-is-set.patch \
           file://fix-linking-of-libraries-used-by-reglib.patch \
"
SRC_URI[crda.md5sum] = "0431fef3067bf503dfb464069f06163a"
SRC_URI[crda.sha256sum] = "43fcb9679f8b75ed87ad10944a506292def13e4afb194afa7aa921b01e8ecdbf"

SRC_URI[bin.md5sum] = "d750c402c5510add7380edcb1d9b75b2"
SRC_URI[bin.sha256sum] = "eab6b50f30748a8b0065ba38cf3df05aac161a5861ae0a6c3cfd01d38a71c9dd"

inherit python-dir pythonnative
# Recursive make problem
EXTRA_OEMAKE = "MAKEFLAGS= DESTDIR=${D} LIBDIR=${libdir}/crda LDLIBREG='-Wl,-rpath,${libdir}/crda -lreg'"

do_compile() {
    oe_runmake all_noverify
}

do_install() {
    oe_runmake SBINDIR=${sbindir}/ install

    install -d ${D}${libdir}/crda/

    install -m 0644 ${WORKDIR}/wireless-regdb-2014.11.18/regulatory.bin ${D}${libdir}/crda/regulatory.bin
}


RDEPENDS_${PN} = "udev"
FILES_${PN} += "${libdir}crda/regulatory.bin \
                ${base_libdir}/udev/rules.d/85-regulatory.rules \
"
