SUMMARY = "Dynamic hash table implementation"
DESCRIPTION = "Dynamic hash table implementation"
HOMEPAGE = "https://fedorahosted.org/released/ding-libs"
SECTION = "base"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://fedorahosted.org/released/${BPN}/${BP}.tar.gz"

inherit autotools pkgconfig

SRC_URI[sha256sum] = "a319a327deb81f2dfab9ce4a4926e80e1dac5dcfc89f4c7e548cec2645af27c1"
