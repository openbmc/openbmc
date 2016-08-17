SUMMARY = "A benchmark for GL(ES)2 based compositing operations"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

DEPENDS = "virtual/libgl virtual/egl virtual/libgles1 virtual/libgles2 \
           virtual/libx11 libxcomposite pixman libxrender libpng12"

SRC_URI = "https://launchpad.net/glcompbench/trunk/${PV}/+download/${BPN}-${PV}.tar.gz \
           file://glbench-compile-fix.patch"

SRC_URI[md5sum] = "c939d9156fe940960098f38707fea827"
SRC_URI[sha256sum] = "b04b738cec06c6786ceafa86e4735fd8b971c078265754854ef356b0379542ee"

inherit pkgconfig waf distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"
