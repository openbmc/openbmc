DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

DEPENDS = "libpng tiff lcms zlib"

SRC_URI = " \
    git://github.com/uclouvain/openjpeg.git \
    file://0001-Ensure-cmake-files-are-installed-at-common-location.patch \
    file://0002-Do-not-ask-cmake-to-export-binaries-they-don-t-make-.patch \
"
SRCREV = "081de4b15f54cb4482035b7bf5e3fb443e4bc84b"
S = "${WORKDIR}/git"

inherit cmake

# for multilib
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_LIB_DIR=${@d.getVar('baselib').replace('/', '')}"
