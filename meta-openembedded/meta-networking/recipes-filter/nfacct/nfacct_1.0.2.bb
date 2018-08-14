SUMMARY = "nfacct is the command line tool to create/retrieve/delete accounting objects"
HOMEPAGE = "http://netfilter.org/projects/nfacct/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "ftp://ftp.netfilter.org/pub/${BPN}/${BP}.tar.bz2"

SRC_URI[md5sum] = "94faafdaaed85ca9220c5692be8a408e"
SRC_URI[sha256sum] = "ecff2218754be318bce3c3a5d1775bab93bf4168b2c4aac465785de5655fbd69"
DEPENDS = "libnfnetlink libmnl libnetfilter-acct"

EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

inherit autotools pkgconfig

