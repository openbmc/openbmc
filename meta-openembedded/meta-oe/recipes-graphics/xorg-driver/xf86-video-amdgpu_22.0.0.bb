require recipes-graphics/xorg-driver/xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=aabff1606551f9461ccf567739af63dc"

SUMMARY = "X.Org X server -- AMD Radeon GPU display driver"

DESCRIPTION = "Xorg driver for AMD Radeon GPUs using the amdgpu kernel driver"

SRC_URI[md5sum] = "73efb437f5eb29b2c52a9d82c7e15d72"
SRC_URI[sha256sum] = "bc47a1a8854e790270fa5de2fb9dfe8558139b03d8f68ac1057dcd235d572dcc"

XORG_DRIVER_COMPRESSOR = ".tar.gz"
DEPENDS += "virtual/libx11 libdrm virtual/libgbm xorgproto"

PACKAGECONFIG ??= "udev glamor"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[glamor] = "--enable-glamor,--disable-glamor"

RRECOMMENDS:${PN} += "linux-firmware-amdgpu"

FILES:${PN}-config = "${datadir}/X11/xorg.conf.d"
FILES:${PN} += "${datadir}/X11"
