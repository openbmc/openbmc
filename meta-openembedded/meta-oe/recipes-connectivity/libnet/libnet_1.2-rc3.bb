SUMMARY = "A packet dissection and creation library"
# libnet at packetfactory.net is dead
HOMEPAGE = "https://github.com/sam-github/libnet"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=3ec839e00408b484d33b472a86b7c266"
DEPENDS = "libpcap"
# There are major API changes beween libnet v1.0 and libnet v1.1
PROVIDES = "libnet-1.2rc2"

SRC_URI = "${SOURCEFORGE_MIRROR}/libnet-dev/${BPN}-${PV}.tar.gz \
           file://0001-Support-musl-libc-remove-support-for-glibc-2.1.patch \
          "

SRC_URI[md5sum] = "f051e6e5bdecddb90f77c701c2ca1804"
SRC_URI[sha256sum] = "72c380785ad44183005e654b47cc12485ee0228d7fa6b0a87109ff7614be4a63"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/libnet-dev/files/"
UPSTREAM_CHECK_REGEX = "libnet-(?P<pver>\d+(\.\d+)+-*[a-z]*\d*)\.tar"

S = "${WORKDIR}/${BPN}-${PV}"

inherit autotools binconfig

