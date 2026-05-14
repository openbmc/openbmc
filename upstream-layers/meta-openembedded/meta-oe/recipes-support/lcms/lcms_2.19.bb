SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e9ce323c4b71c943a785db90142b228a"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[sha256sum] = "49e7e134e4299733dd0eda434fa468997a28ab3d33fa397c642b03644f552216"

DEPENDS = "tiff"

BBCLASSEXTEND = "native nativesdk"

S = "${UNPACKDIR}/lcms2-${PV}"

inherit autotools sourceforge-releases

CVE_PRODUCT += "littlecms:little_cms_color_engine"
