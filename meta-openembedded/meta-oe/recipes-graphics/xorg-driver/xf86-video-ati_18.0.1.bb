require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- ATI Radeon video driver"

DESCRIPTION = "Open-source X.org graphics driver for ATI Radeon graphics"

DEPENDS += "virtual/libx11 libxvmc drm \
            virtual/libgl xorgproto libpciaccess"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES += "opengl"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

SRC_URI[md5sum] = "40e7c0a5a69aba3d84e0958f58705ea7"
SRC_URI[sha256sum] = "72ea3b8127d4550b64f797457f5a7851a541fa4ee2cc3f345b6c1886b81714a0"

EXTRA_OECONF += "--disable-glamor"

RDEPENDS_${PN} += "xserver-xorg-module-exa"
RRECOMMENDS_${PN} += "linux-firmware-radeon"

FILES_${PN} += "${datadir}/X11"
