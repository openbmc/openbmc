DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org/index.php?menu=main"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}.mirror/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "f6419fcc233df84f9a81eb36633c6db6"
SRC_URI[sha256sum] = "1232bb814fd88d8ed314c94f0bfebb03de8559583a33abbe8c64ef3fc0a8ff03"

inherit cmake

DEPENDS = "libpng tiff lcms"

# standard path for *.cmake
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_PACKAGE_DIR=${baselib}/cmake \
                  -DOPENJPEG_INSTALL_LIB_DIR:PATH=${libdir}"

FILES_${PN}-dev += "${libdir}/cmake/*.cmake"
