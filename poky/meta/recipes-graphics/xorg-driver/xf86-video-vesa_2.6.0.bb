require xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a1f0610ebdc6f314a9fa5102a8c5c1b0"

SUMMARY = "X.Org X server -- Generic Vesa video driver"

DESCRIPTION = "vesa is an Xorg driver for generic VESA video cards. It \
can drive most VESA-compatible video cards, but only makes use of the \
basic standard VESA core that is common to these cards. The driver \
supports depths 8, 15 16 and 24."

DEPENDS += "virtual/libx11 xorgproto libpciaccess"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

RRECOMMENDS:${PN} += "xserver-xorg-module-libint10"

XORG_DRIVER_COMPRESSOR = ".tar.xz"
SRC_URI[sha256sum] = "10082771b1a8f659c60b3d9b70456a4b59948a75b3cade2dfa33b8b05e549bb1"

