require recipes-graphics/xorg-driver/xorg-driver-video.inc

SUMMARY = "X.Org X server -- Matrox MGA display driver"

DESCRIPTION = "mga is an Xorg driver for Matrox video cards"

LIC_FILES_CHKSUM = "file://COPYING;md5=4aa220f495ce9be5ce4243d21ebac14f"

DEPENDS += "virtual/libx11 libpciaccess"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

XORG_DRIVER_COMPRESSOR = ".tar.xz"
SRC_URI[sha256sum] = "7bf85dbf4efa17b7cdd483be8f61cf11fd73d0310514b6ae10d89358de63ab73"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri', '', d)}"
PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,drm xorgproto,xserver-xorg-extension-dri"

RDEPENDS:${PN} = "xserver-xorg-module-exa"
