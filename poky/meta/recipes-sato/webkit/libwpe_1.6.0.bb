SUMMARY = "General-purpose library specifically developed for the WPE-flavored port of WebKit."
HOMEPAGE = "https://github.com/WebPlatformForEmbedded/libwpe"
BUGTRACKER = "https://github.com/WebPlatformForEmbedded/libwpe/issues"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=371a616eb4903c6cb79e9893a5f615cc"
DEPENDS = "virtual/egl libxkbcommon"

# Workaround build issue with RPi userland EGL libraries.
CFLAGS_append_rpi = " ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', '-D_GNU_SOURCE', d)}"

inherit cmake features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[md5sum] = "6e8a2c279dcc3617db5ec7ac4c03d628"
SRC_URI = "https://wpewebkit.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "3587c6b8a807f4bb76b268ba74ca82c6b395b90235db41ad8252224456193c90"
