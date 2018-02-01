require xorg-driver-video.inc

SUMMARY = "X.Org X server -- VMware SVGA display driver"

DESCRIPTION = "vmware is an Xorg driver for VMware virtual video cards."

LIC_FILES_CHKSUM = "file://COPYING;md5=5fcd7d437a959a15fbee8707747c6b53"

DEPENDS += "virtual/libx11 xineramaproto videoproto libpciaccess"

SRC_URI += "file://0002-add-option-for-vmwgfx.patch"

SRC_URI[md5sum] = "4c3912e4d8947f6c2fc1ee9e2f211d74"
SRC_URI[sha256sum] = "e2f7f7101fba7f53b268e7a25908babbf155b3984fb5268b3d244eb6c11bf62b"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

PACKAGECONFIG ?= ""
PACKAGECONFIG[vmwgfx] = "--enable-vmwgfx, --disable-vmwgfx, libdrm virtual/mesa"
