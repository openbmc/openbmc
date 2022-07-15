SUMMARY = "Count blank lines, comment lines, and physical lines of source code \
in many programming languages."
AUTHOR = "Al Danial"

LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

SRC_URI = "https://github.com/AlDanial/cloc/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "08212290c4e9b21c7bb1abc0a9b4a365ce1c5eb0d8f3ebb74d50b29559a71a9c"

UPSTREAM_CHECK_URI = "https://github.com/AlDanial/${BPN}/releases"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/cloc ${D}${bindir}/cloc
}

RDEPENDS:${PN} = "perl perl-modules"
