SUMMARY = "Portable Bandwidth Monitor and rate estimator"
HOMEPAGE = "https://github.com/Jafaral/bmon"
SECTION = "net"

LICENSE = "BSD-2-Clause AND MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=544799d0b492f119fa04641d1b8868ed \
 file://LICENSE.BSD;md5=5c262c13b60ebefe3060aed37d334ab6 \
                   "
DEPENDS = "libconfuse libnl ncurses"

SRC_URI = "https://github.com/Jafaral/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "6016e41fdda9166b86b09a0ac601a0c453c3b35acdf904c97a8a555459ad73c0"

inherit autotools pkgconfig

