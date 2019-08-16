SUMMARY = "Netfilter Tables userspace utillites"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"

DEPENDS = "libmnl libnftnl readline gmp bison-native"

UPSTREAM_CHECK_URI = "https://www.netfilter.org/projects/nftables/files/"

SRC_URI = "http://www.netfilter.org/projects/nftables/files/${BP}.tar.bz2"
SRC_URI[md5sum] = "e2facbcad6c5d9bd87a0bf5081a31522"
SRC_URI[sha256sum] = "ead3bb68ed540bfbb87a96f2b69c3d65ab0c2a0c3f6e739a395c09377d1b4fce"

inherit autotools manpages pkgconfig

PACKAGECONFIG ?= "python"
PACKAGECONFIG[man] = "--enable--man-doc, --disable-man-doc"
PACKAGECONFIG[python] = "--with-python-bin=${PYTHON}, --with-python-bin="", python3"

inherit ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3native', '', d)}

ASNEEDED = ""

RRECOMMENDS_${PN} += "kernel-module-nf-tables"

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${libdir_native}/${PYTHON_DIR}"
RDEPENDS_${PN}-python = "python3-core python3-json"
