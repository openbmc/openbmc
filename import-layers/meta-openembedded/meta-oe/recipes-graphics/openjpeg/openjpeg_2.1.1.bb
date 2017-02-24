DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

SRC_URI = "https://github.com/uclouvain/${BPN}/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "0cc4b2aee0a9b6e9e21b7abcd201a3ec"
SRC_URI[sha256sum] = "82c27f47fc7219e2ed5537ac69545bf15ed8c6ba8e6e1e529f89f7356506dbaa"

inherit cmake

DEPENDS = "libpng tiff lcms zlib"

# standard path for *.cmake
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_PACKAGE_DIR=${baselib}/cmake \
                  -DOPENJPEG_INSTALL_LIB_DIR:PATH=${libdir}"

FILES_${PN}-dev += "${libdir}/cmake/*.cmake"
