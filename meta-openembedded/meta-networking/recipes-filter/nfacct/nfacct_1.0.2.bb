SUMMARY = "nfacct is the command line tool to create/retrieve/delete accounting objects"
HOMEPAGE = "http://netfilter.org/projects/nfacct/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

UPSTREAM_CHECK_URI = "https://www.netfilter.org/pub/nfacct"
SRC_URI = "https://www.netfilter.org/pub/${BPN}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "ecff2218754be318bce3c3a5d1775bab93bf4168b2c4aac465785de5655fbd69"

DEPENDS = "libnfnetlink libmnl libnetfilter-acct"

EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

inherit autotools pkgconfig

