SUMMARY = "Count blank lines, comment lines, and physical lines of source code \
in many programming languages."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

SRC_URI = "git://github.com/AlDanial/cloc.git;protocol=https;branch=master"
SRCREV = "eb5cef64db2b2d4380f501568cc584b7cab4ba50"

UPSTREAM_CHECK_URI = "https://github.com/AlDanial/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/cloc ${D}${bindir}/cloc
}

RDEPENDS:${PN} = "perl perl-modules"
