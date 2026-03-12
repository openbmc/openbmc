SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "https://proj.org/en/stable/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27445198ba1500f508fce2b183ce0ff"
DEPENDS = "sqlite3 sqlite3-native"

SRC_URI = "https://download.osgeo.org/${BPN}/${BP}.tar.gz"
SRC_URI[sha256sum] = "65705ecd987b50bf63e15820ce6bd17c042feaabda981249831bd230f6689709"

inherit bash-completion cmake lib_package pkgconfig

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF"

FILES:${PN} += "${datadir}/proj"

BBCLASSEXTEND = "native"

PACKAGECONFIG ?= "curl shared"
PACKAGECONFIG:class-native = "curl shared apps"

PACKAGECONFIG[apps] = "-DBUILD_APPS=ON, -DBUILD_APPS=OFF"
PACKAGECONFIG[curl] = "-DENABLE_CURL=ON,-DENABLE_CURL=OFF,curl"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF,,"
PACKAGECONFIG[tiff] = "-DENABLE_TIFF=ON,-DENABLE_TIFF=OFF,tiff"

BBCLASSEXTEND = "native nativesdk"
