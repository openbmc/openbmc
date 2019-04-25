require recipes-graphics/xorg-driver/xorg-driver-video.inc

SUMMARY = "X.Org X server -- Matrox MGA display driver"

DESCRIPTION = "mga is an Xorg driver for Matrox video cards"

LIC_FILES_CHKSUM = "file://COPYING;md5=bc1395d2cd32dfc5d6c57d2d8f83d3fc"

SRC_URI += "file://checkfile.patch"

DEPENDS += "virtual/libx11 libpciaccess"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

SRC_URI[md5sum] = "b8fc99b4adea8bfe80156b13df4b6c9c"
SRC_URI[sha256sum] = "268946e1a13e9d80e4f724a0740df9e6e8c8bad37697fcbf456924e9fdbb5d79"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri', '', d)}"
PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,drm xorgproto,xserver-xorg-extension-dri"

RDEPENDS_${PN} = "xserver-xorg-module-exa"
