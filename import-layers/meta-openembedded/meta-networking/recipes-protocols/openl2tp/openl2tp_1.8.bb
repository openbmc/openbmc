SUMMARY = "An L2TP client/server, designed for VPN use."
DESCRIPTION = "OpenL2TP is an open source L2TP client / server, written \
specifically for Linux. It has been designed for use as an enterprise \
L2TP VPN server or in commercial, Linux-based, embedded networking \
products and is able to support hundreds of sessions, each with \
different configuration. It is used by several ISPs to provide \
L2TP services and by corporations to implement L2TP VPNs."
HOMEPAGE = "http://www.openl2tp.org/"
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9d9259cbbf00945adc25a470c1d3585"
DEPENDS = "popt flex readline"

SRC_URI = "http://ftp.jaist.ac.jp/pub/sourceforge/o/op/${PN}/${PN}/${PV}/${BP}.tar.gz \
           file://Makefile-modify-CFLAGS-to-aviod-build-error.patch \
           file://openl2tp-simplify-gcc-warning-hack.patch \
          "
SRC_URI[md5sum] = "e3d08dedfb9e6a9a1e24f6766f6dadd0"
SRC_URI[sha256sum] = "1c97704d4b963a87fbc0e741668d4530933991515ae9ab0dffd11b5444f4860f"

inherit autotools-brokensep pkgconfig

PARALLEL_MAKE = ""
EXTRA_OEMAKE = 'CFLAGS="${CFLAGS} -Wno-nused-but-set-variable"'

do_compile_prepend() {
    sed -i -e "s:SYS_LIBDIR=.*:SYS_LIBDIR=${libdir}:g" \
        -e 's:$(CROSS_COMPILE)as:${AS}:g' \
        -e 's:$(CROSS_COMPILE)ld:${LD}:g' \
        -e 's:$(CROSS_COMPILE)gcc:${CC}:g' \
        -e 's:$(CROSS_COMPILE)ar:${AR}:g' \
        -e 's:$(CROSS_COMPILE)nm:${NM}:g' \
        -e 's:$(CROSS_COMPILE)strip:${STRIP}:g' \
        -e 's:$(CROSS_COMPILE)install:install:g' \
        ${S}/Makefile
}
