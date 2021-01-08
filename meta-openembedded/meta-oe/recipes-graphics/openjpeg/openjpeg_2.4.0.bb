DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

DEPENDS = "libpng tiff lcms zlib"

SRC_URI = " \
    git://github.com/uclouvain/openjpeg.git \
    file://0002-Do-not-ask-cmake-to-export-binaries-they-don-t-make-.patch \
    file://0001-This-patch-fixed-include-dir-to-usr-include-.-Obviou.patch \
"
SRCREV = "37ac30ceff6640bbab502388c5e0fa0bff23f505"
S = "${WORKDIR}/git"

inherit cmake

# for multilib
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_LIB_DIR=${@d.getVar('baselib').replace('/', '')}"

FILES_${PN} += "${libdir}/openjpeg*"
