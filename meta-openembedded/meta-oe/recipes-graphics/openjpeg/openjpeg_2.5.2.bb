DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

DEPENDS = "libpng tiff lcms zlib"

SRC_URI = "git://github.com/uclouvain/openjpeg.git;branch=master;protocol=https \
           file://0001-Do-not-ask-cmake-to-export-binaries-they-don-t-make-.patch \
           "
SRCREV = "39e8c50a2f9bdcf36810ee3d41bcbf1cc78968ae"
S = "${WORKDIR}/git"

inherit cmake

# for multilib
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_LIB_DIR=${@d.getVar('baselib').replace('/', '')}"

FILES:${PN} += "${libdir}/openjpeg*"

BBCLASSEXTEND = "native"
