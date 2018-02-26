require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- ATI Radeon video driver"

DESCRIPTION = "Open-source X.org graphics driver for ATI Radeon graphics"

DEPENDS += "virtual/libx11 libxvmc drm xf86driproto glproto \
            virtual/libgl xineramaproto libpciaccess"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES += "opengl"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

SRC_URI[md5sum] = "f34d04a755e761e03b459155fa3ddcbb"
SRC_URI[sha256sum] = "401f5de772928f3dc4ce43a885adb0a47a2f61aa4a9e45d2ab3d184136a9d6fa"

EXTRA_OECONF += "--disable-glamor"

RDEPENDS_${PN} += "xserver-xorg-module-exa"
RRECOMMENDS_${PN} += "linux-firmware-radeon"
