HOMEPAGE = "https://github.com/embear-engineering/drm-framebuffer"
SUMMARY = "A simple application which can be used to test a Linux DRM device"
SECTION = "graphics"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7702f203b58979ebbc31bfaeb44f219c"

SRC_URI = "git://github.com/embear-engineering/drm-framebuffer;protocol=https;branch=copilot/create-release-v1-0-0"

SRCREV = "09df30960552f23d0fa7471be0caf7f095fcd522"

inherit cmake pkgconfig 

DEPENDS = "libdrm"

