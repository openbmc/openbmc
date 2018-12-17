require xorg-driver-video.inc

SUMMARY = "X.Org X server -- VMware SVGA display driver"

DESCRIPTION = "vmware is an Xorg driver for VMware virtual video cards."

LIC_FILES_CHKSUM = "file://COPYING;md5=5fcd7d437a959a15fbee8707747c6b53"

DEPENDS += "virtual/libx11 xorgproto libpciaccess"

SRC_URI += "file://0002-add-option-for-vmwgfx.patch"

SRC_URI[md5sum] = "08d66d062055080ff699ab4869726ea2"
SRC_URI[sha256sum] = "47971924659e51666a757269ad941a059ef5afe7a47b5101c174a6022ac4066c"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

PACKAGECONFIG ?= ""
PACKAGECONFIG[vmwgfx] = "--enable-vmwgfx, --disable-vmwgfx, libdrm virtual/mesa"
