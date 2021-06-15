require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94"

SUMMARY = "X.Org X server -- nouveau video driver"

DESCRIPTION = "Open-source X.org graphics driver for NVIDIA graphics"

DEPENDS += "virtual/libx11 libxvmc drm xorgproto \
            virtual/libgl libpciaccess"
RDEPENDS_${PN} += "xserver-xorg-module-exa"

inherit features_check
REQUIRED_DISTRO_FEATURES += "opengl"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

SRC_URI[md5sum] = "ecd9be89d853301167e3d564c49f7a8e"
SRC_URI[sha256sum] = "304060806415579cdb5c1f71f1c54d11cacb431b5552b170decbc883ed43bf06"
