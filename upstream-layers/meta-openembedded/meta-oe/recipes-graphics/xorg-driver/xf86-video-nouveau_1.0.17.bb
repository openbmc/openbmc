require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94"

SUMMARY = "X.Org X server -- nouveau video driver"

DESCRIPTION = "Open-source X.org graphics driver for NVIDIA graphics"

DEPENDS += "virtual/libx11 libxvmc drm xorgproto \
            virtual/libgl libpciaccess"
RDEPENDS:${PN} += "xserver-xorg-module-exa"

inherit features_check
REQUIRED_DISTRO_FEATURES += "opengl"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

CFLAGS += "-Wno-error=implicit-function-declaration"

SRC_URI += "file://0001-nouveau-fixup-driver-for-new-X-server-ABI.patch"
SRC_URI[sha256sum] = "499322e27a55c8183166bf2dd1e47d085eb834143e0d7036baba8427b90c156b"
