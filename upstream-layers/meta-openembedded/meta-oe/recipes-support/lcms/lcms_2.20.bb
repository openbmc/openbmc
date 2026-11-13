SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e9ce323c4b71c943a785db90142b228a"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "18ff5504fa9cc8a2b1f3591180d90ead16c8a2ea78e530523b46eab70e1ca6b9"

S = "${UNPACKDIR}/lcms2-${PV}"

inherit meson pkgconfig sourceforge-releases lib_package ptest

PACKAGECONFIG ??= "jpeg tiff"
PACKAGECONFIG[jpeg] = "-Djpeg=enabled,-Djpeg=disabled,jpeg"
PACKAGECONFIG[tiff] = "-Dtiff=enabled,-Dtiff=disabled,tiff"

EXTRA_OEMESON = "-Dutils=true"

do_compile_ptest() {
    ninja --verbose ${PARALLEL_MAKE} testbed/testcms
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${B}/testbed/testcms ${D}${PTEST_PATH}/
    install -m 0644 ${S}/testbed/*.icc ${D}${PTEST_PATH}/
}

CVE_PRODUCT += "littlecms:little_cms_color_engine"

BBCLASSEXTEND = "native nativesdk"
