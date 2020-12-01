SUMMARY = "Count blank lines, comment lines, and physical lines of source code \
in many programming languages."
AUTHOR = "Al Danial"

LICENSE="GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

SRC_URI = "https://github.com/AlDanial/cloc/releases/download/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "9f12f29c824ef61c5eaba6cf2dd6e183"
SRC_URI[sha256sum] = "e4e30f083bf4e4a5efbe29efa0f6cefa223ba4e841ad1337653ad1f52702dc6f"

UPSTREAM_CHECK_URI = "https://github.com/AlDanial/${BPN}/releases"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/cloc ${D}${bindir}/cloc
}

RDEPENDS_${PN} = "perl perl-modules"
