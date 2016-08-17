SUMMARY = "Threaded I/O tester"
HOMEPAGE = "http://sourceforge.net/projects/tiobench/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

PR = "r1"

SRC_URI = "\
    http://sourceforge.net/projects/tiobench/files/tiobench/${PV}/${BP}.tar.gz \
    file://tiobench-makefile.patch \
    file://avoid-glibc-clashes.patch \
    file://0001-Drop-inline-of-crc32-function-to-fix-build-using-GCC.patch \
"
SRC_URI[md5sum] = "bf485bf820e693c79e6bd2a38702a128"
SRC_URI[sha256sum] = "8ad011059a35ac70cdb5e3d3999ceee44a8e8e9078926844b0685b7ea9db2bcc"

EXTRA_OEMAKE = "PREFIX=${D}/usr"

do_install() {
    oe_runmake install
}

RDEPENDS_${PN} = "\
    perl \
    perl-module-exporter-heavy \
    perl-module-getopt-long \
    perl-module-overload \
    perl-module-strict \
"
