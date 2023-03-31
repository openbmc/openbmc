require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- AMD Radeon GPU display driver"

DESCRIPTION = "Xorg driver for AMD Radeon GPUs using the amdgpu kernel driver"

SRC_URI[sha256sum] = "08c38287d39b999fd61ecb6e7b23d5079762e2b4b2179b3567973ed9aaf71222"

XORG_DRIVER_COMPRESSOR = ".tar.gz"
DEPENDS += "virtual/libx11 libdrm virtual/libgbm xorgproto"

REQUIRED_DISTRO_FEATURES += "opengl"

PACKAGECONFIG ??= "udev glamor"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[glamor] = "--enable-glamor,--disable-glamor"

RRECOMMENDS:${PN} += "linux-firmware-amdgpu"

FILES:${PN}-config = "${datadir}/X11/xorg.conf.d"
FILES:${PN} += "${datadir}/X11"
