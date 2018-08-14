require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94"

SUMMARY = "X.Org X server -- nouveau video driver"

DESCRIPTION = "Open-source X.org graphics driver for NVIDIA graphics"

DEPENDS += "virtual/libx11 libxvmc drm xf86driproto glproto \
            virtual/libgl xineramaproto libpciaccess"
RDEPENDS_${PN} += "xserver-xorg-module-exa"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES += "opengl"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

SRC_URI[md5sum] = "717203cb87029cddcbccf7398f9ad8c3"
SRC_URI[sha256sum] = "aede10fd395610a328697adca3434fb14e9afbd79911d6c8545cfa2c0e541d4c"

