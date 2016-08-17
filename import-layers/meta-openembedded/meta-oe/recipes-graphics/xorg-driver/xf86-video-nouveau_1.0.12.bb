require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94"

SUMMARY = "X.Org X server -- nouveau video driver"

DESCRIPTION = "Open-source X.org graphics driver for NVIDIA graphics"

DEPENDS += "virtual/libx11 libxvmc drm xf86driproto glproto \
            virtual/libgl xineramaproto libpciaccess"
RDEPENDS_${PN} += "xserver-xorg-module-exa"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

SRC_URI[md5sum] = "dc79910c7b9e32321cefc7af250c7765"
SRC_URI[sha256sum] = "0ea057ad7fc31caba2d4e46c7e418fe2b3c762b04fb8d382f53383397fd8391e"

