SUMMARY = "Count blank lines, comment lines, and physical lines of source code \
in many programming languages."

LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

SRC_URI = "https://github.com/AlDanial/cloc/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "5fe0b159eb75718df7308a4f61470eaddf82170733929999e37a3fbb4651cf8a"

UPSTREAM_CHECK_URI = "https://github.com/AlDanial/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/cloc ${D}${bindir}/cloc
}

RDEPENDS:${PN} = "perl perl-modules"
