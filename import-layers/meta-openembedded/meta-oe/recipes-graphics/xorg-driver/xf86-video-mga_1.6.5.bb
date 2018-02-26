require recipes-graphics/xorg-driver/xorg-driver-video.inc

SUMMARY = "X.Org X server -- Matrox MGA display driver"

DESCRIPTION = "mga is an Xorg driver for Matrox video cards"

LIC_FILES_CHKSUM = "file://COPYING;md5=bc1395d2cd32dfc5d6c57d2d8f83d3fc"

SRC_URI += "file://checkfile.patch"

DEPENDS += "virtual/libx11 libpciaccess"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

SRC_URI[md5sum] = "3ee2549247e01de3e7bce52c27483118"
SRC_URI[sha256sum] = "b663cd8e6364f7c4e2637b9fcab9861d0e3971518c73b00d213f6545a1289422"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri', '', d)}"
PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,drm xf86driproto,xserver-xorg-extension-dri"

RDEPENDS_${PN} = "xserver-xorg-module-exa"
