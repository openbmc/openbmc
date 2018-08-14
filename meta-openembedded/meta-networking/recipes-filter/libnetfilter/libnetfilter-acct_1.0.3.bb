SUMMARY = "libnetfilter_acct accounting infrastructure."
DESCRIPTION = "libnetfilter_acct is the userspace library providing interface to extended accounting infrastructure."
HOMEPAGE = "http://netfilter.org/projects/libnetfilter_acct/index.html"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
DEPENDS = "libnfnetlink libmnl"

SRC_URI = "http://ftp.netfilter.org/pub/libnetfilter_acct/libnetfilter_acct-1.0.3.tar.bz2 \
           file://0001-libnetfilter-acct-Declare-the-define-visivility-attribute-together.patch \
"
SRC_URI[md5sum] = "814b2972b2f5c740ff87510bc109168b"
SRC_URI[sha256sum] = "4250ceef3efe2034f4ac05906c3ee427db31b9b0a2df41b2744f4bf79a959a1a"

S = "${WORKDIR}/libnetfilter_acct-${PV}"

inherit autotools pkgconfig
