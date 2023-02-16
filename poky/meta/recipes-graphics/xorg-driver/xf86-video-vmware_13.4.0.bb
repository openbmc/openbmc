require xorg-driver-video.inc

SUMMARY = "X.Org X server -- VMware SVGA display driver"

DESCRIPTION = "vmware is an Xorg driver for VMware virtual video cards."

LIC_FILES_CHKSUM = "file://COPYING;md5=5fcd7d437a959a15fbee8707747c6b53"

DEPENDS += "virtual/libx11 xorgproto libpciaccess"
XORG_DRIVER_COMPRESSOR = ".tar.xz"

SRC_URI[sha256sum] = "aed31ee5ed5ecc6e2226705383e7ad06f7602c1376a295305f376b17af3eb81a"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'
