SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e9ce323c4b71c943a785db90142b228a"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "bfc54f7bab59fbc921012014a8032e4cba4abd46db47d46b76416a8c0b2815c8"

DEPENDS = "tiff"

BBCLASSEXTEND = "native nativesdk"

S = "${UNPACKDIR}/lcms2-${PV}"

inherit autotools sourceforge-releases ptest

do_compile_ptest() {
    oe_runmake -C ${B}/testbed testcms
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${B}/testbed/testcms ${D}${PTEST_PATH}/
    install -m 0644 ${S}/testbed/*.icc ${D}${PTEST_PATH}/
}

RDEPENDS:${PN}-ptest += "bash"

CVE_PRODUCT += "littlecms:little_cms_color_engine"
