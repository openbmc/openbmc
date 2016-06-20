require xorg-driver-video.inc

SUMMARY = "X.Org X server -- VMware SVGA display driver"

DESCRIPTION = "vmware is an Xorg driver for VMware virtual video cards."

LIC_FILES_CHKSUM = "file://COPYING;md5=5fcd7d437a959a15fbee8707747c6b53"

DEPENDS += "virtual/libx11 xineramaproto videoproto libpciaccess"

SRC_URI += "file://0002-add-option-for-vmwgfx.patch"

SRC_URI[md5sum] = "0cba22fed4cb639d5c4276f7892c543d"
SRC_URI[sha256sum] = "3c1d244e4b1b77e92126957965cdc9fb82de4c215c0706a3a8aaff6939e4a0cc"

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

PACKAGECONFIG ?= ""
PACKAGECONFIG[vmwgfx] = "--enable-vmwgfx, --disable-vmwgfx, libdrm virtual/mesa"
