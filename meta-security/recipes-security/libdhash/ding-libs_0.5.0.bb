SUMMARY = "Dynamic hash table implementation"
DESCRIPTION = "Dynamic hash table implementation"
HOMEPAGE = "https://fedorahosted.org/released/ding-libs"
SECTION = "base"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://fedorahosted.org/released/${BPN}/${BP}.tar.gz"

inherit autotools pkgconfig

SRC_URI[md5sum] = "786f2880d30136a61df02e5d740ddc6e"
SRC_URI[sha256sum] = "dab937537a05d7a7cbe605fdb9b3809080d67b124ac97eb321255b35f5b172fd"
