DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

DEPENDS = "libpng tiff lcms zlib"

SRC_URI = "git://github.com/uclouvain/openjpeg.git;branch=master;protocol=https \
           file://0001-Do-not-ask-cmake-to-export-binaries-they-don-t-make-.patch \
           "
SRCREV = "210a8a5690d0da66f02d49420d7176a21ef409dc"
S = "${WORKDIR}/git"

inherit cmake

# for multilib
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_LIB_DIR=${@d.getVar('baselib').replace('/', '')}"

FILES:${PN} += "${libdir}/openjpeg*"

BBCLASSEXTEND = "native"
