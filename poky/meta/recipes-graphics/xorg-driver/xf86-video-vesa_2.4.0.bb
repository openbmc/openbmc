require xorg-driver-video.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a1f0610ebdc6f314a9fa5102a8c5c1b0"

SUMMARY = "X.Org X server -- Generic Vesa video driver"

DESCRIPTION = "vesa is an Xorg driver for generic VESA video cards. It \
can drive most VESA-compatible video cards, but only makes use of the \
basic standard VESA core that is common to these cards. The driver \
supports depths 8, 15 16 and 24."

DEPENDS += "virtual/libx11 xorgproto libpciaccess"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

RRECOMMENDS_${PN} += "xserver-xorg-module-libint10"

SRC_URI[md5sum] = "8134201beaf6f77150c7809c3cc802e6"
SRC_URI[sha256sum] = "bf443c94d7bf6cd4e248f8a3147f4647be04dc4c80250d9405006263bbdee38c"

SRC_URI += " \
    file://0001-Refuse-to-run-on-UEFI-machines.patch \
    "
