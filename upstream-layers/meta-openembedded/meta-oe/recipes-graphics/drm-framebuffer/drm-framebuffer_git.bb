HOMEPAGE = "https://github.com/embear-engineering/drm-framebuffer"
SUMMARY = "A simple application which can be used to test a Linux DRM device"
SECTION = "graphics"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7702f203b58979ebbc31bfaeb44f219c"

SRC_URI = "git://github.com/embear-engineering/drm-framebuffer;protocol=https;branch=main"

PV = "0.1+git"
SRCREV = "a560c2b810c8f677aaaaa9e9e8b1d13854718aba"

inherit cmake pkgconfig 

DEPENDS = "libdrm"

