require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- ATI Radeon video driver"

DESCRIPTION = "Open-source X.org graphics driver for ATI Radeon graphics"

DEPENDS += "virtual/libx11 libxvmc drm \
            virtual/libgl xorgproto libpciaccess"

REQUIRED_DISTRO_FEATURES += "opengl"

XORG_DRIVER_COMPRESSOR = ".tar.xz"
SRC_URI[sha256sum] = "c8c8bb56d3f6227c97e59c3a3c85a25133584ceb82ab5bc05a902a743ab7bf6d"

PACKAGECONFIG ??= "udev"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[glamor] = "--enable-glamor,--disable-glamor"

RDEPENDS:${PN}:append = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'glamor', 'libegl', '', d)} \
    mesa-megadriver \
    xserver-xorg-extension-glx \
    xserver-xorg-module-exa \
"

RRECOMMENDS:${PN} += "linux-firmware-radeon"

PACKAGES =+ "${PN}-config"

FILES:${PN}-config = "${datadir}/X11/xorg.conf.d"
FILES:${PN} += "${datadir}/X11"
