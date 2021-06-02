SUMMARY = "Count blank lines, comment lines, and physical lines of source code \
in many programming languages."
AUTHOR = "Al Danial"

LICENSE="GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

SRC_URI = "https://github.com/AlDanial/cloc/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "e2364c2c9c9eda1d20890c19911c51fb"
SRC_URI[sha256sum] = "152502a61d4bae4a406a05a01bf52489b310ec03dbd8c645d0bb051fa4717ddb"

UPSTREAM_CHECK_URI = "https://github.com/AlDanial/${BPN}/releases"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/cloc ${D}${bindir}/cloc
}

RDEPENDS_${PN} = "perl perl-modules"
