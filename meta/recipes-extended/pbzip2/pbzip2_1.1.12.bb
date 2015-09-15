SUMMARY = "PBZIP2 is a parallel implementation of bzip2"
DESCRIPTION = "PBZIP2 is a parallel implementation of the bzip2 block-sorting \
file compressor that uses pthreads and achieves near-linear speedup on SMP \
machines. The output of this version is fully compatible with bzip2 v1.0.2 or \
newer (ie: anything compressed with pbzip2 can be decompressed with bzip2)."
HOMEPAGE = "http://compression.ca/pbzip2/"
SECTION = "console/utils"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c4f2edec9679d5abef3514a816b54a4"

DEPENDS = "bzip2"
DEPENDS_append_class-native = " bzip2-replacement-native"

SRC_URI = "https://launchpad.net/${BPN}/1.1/${PV}/+download/${BP}.tar.gz"

SRC_URI[md5sum] = "91a4911b13305850423840eb0fa6f4f9"
SRC_URI[sha256sum] = "573bb358a5a7d3bf5f42f881af324cedf960c786e8d66dd03d448ddd8a0166ee"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "CXX='${CXX} ${CXXFLAGS}' LDFLAGS='${LDFLAGS}'"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 pbzip2 ${D}${bindir}/
}

BBCLASSEXTEND = "native"
