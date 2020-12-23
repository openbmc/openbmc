SUMMARY = "PBZIP2 is a parallel implementation of bzip2"
DESCRIPTION = "PBZIP2 is a parallel implementation of the bzip2 block-sorting \
file compressor that uses pthreads and achieves near-linear speedup on SMP \
machines. The output of this version is fully compatible with bzip2 v1.0.2 or \
newer (ie: anything compressed with pbzip2 can be decompressed with bzip2)."
HOMEPAGE = "https://launchpad.net/pbzip2/"
SECTION = "console/utils"
LICENSE = "bzip2-1.0.6"
LIC_FILES_CHKSUM = "file://COPYING;md5=398b8832c6f840cfebd20ab2be6a3743"

DEPENDS = "bzip2"
DEPENDS_append_class-native = " bzip2-replacement-native"

SRC_URI = "https://launchpad.net/${BPN}/1.1/${PV}/+download/${BP}.tar.gz \
           file://0001-pbzip2-Fix-invalid-suffix-on-literal-C-11-warning.patch \
           "

SRC_URI[md5sum] = "4cb87da2dba05540afce162f34b3a9a6"
SRC_URI[sha256sum] = "8fd13eaaa266f7ee91f85c1ea97c86d9c9cc985969db9059cdebcb1e1b7bdbe6"

UPSTREAM_CHECK_URI = "https://launchpad.net/pbzip2/+milestones"
UPSTREAM_CHECK_REGEX = "pbzip2 (?P<pver>\d+(\.\d+)+)"

EXTRA_OEMAKE = "CXX='${CXX} ${CXXFLAGS}' LDFLAGS='${LDFLAGS}'"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 pbzip2 ${D}${bindir}/
}

BBCLASSEXTEND = "native nativesdk"
