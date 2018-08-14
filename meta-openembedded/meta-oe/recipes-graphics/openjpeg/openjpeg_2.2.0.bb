DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

SRC_URI = "https://github.com/uclouvain/${BPN}/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://0001-bmp_read_info_header-reject-bmp-files-with-biBitCoun.patch \
          "
SRC_URI[md5sum] = "269bb0b175476f3addcc0d03bd9a97b6"
SRC_URI[sha256sum] = "6fddbce5a618e910e03ad00d66e7fcd09cc6ee307ce69932666d54c73b7c6e7b"

inherit cmake

DEPENDS = "libpng tiff lcms zlib"

# standard path for *.cmake
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_PACKAGE_DIR=${baselib}/cmake \
                  -DOPENJPEG_INSTALL_LIB_DIR:PATH=${libdir}"

FILES_${PN}-dev += "${libdir}/cmake/*.cmake"
