SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e9ce323c4b71c943a785db90142b228a"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[sha256sum] = "d873d34ad8b9b4cea010631f1a6228d2087475e4dc5e763eb81acc23d9d45a51"

DEPENDS = "tiff"

BBCLASSEXTEND = "native nativesdk"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools

CVE_PRODUCT += "littlecms:little_cms_color_engine"
