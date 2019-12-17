SUMMARY = "Netfilter Tables userspace utillites"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"

DEPENDS = "libmnl libnftnl readline gmp bison-native"

UPSTREAM_CHECK_URI = "https://www.netfilter.org/projects/nftables/files/"

SRC_URI = "http://www.netfilter.org/projects/nftables/files/${BP}.tar.bz2"
SRC_URI[md5sum] = "dfe130724d7c998eb26b56447e932899"
SRC_URI[sha256sum] = "5cb66180143e6bfc774f4eb316206d40ac1cb6df269a90882404cbf7165513f5"

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
