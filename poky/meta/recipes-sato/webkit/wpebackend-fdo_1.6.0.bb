SUMMARY = "WPE's backend based on a freedesktop.org stack."
HOMEPAGE = "https://github.com/Igalia/WPEBackend-fdo"
BUGTRACKER = "https://github.com/Igalia/WPEBackend-fdo/issues"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f62cef2e3645e3e74eb05fd389d7a66"
DEPENDS = "glib-2.0 libxkbcommon wayland virtual/egl libwpe"

DEPENDS_append_class-target = " wayland-native"

inherit cmake features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[md5sum] = "456afeed22f6749f7b2a97c11660835d"
SRC_URI = "https://wpewebkit.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "7f5bd7b9d8f97b1655f4dcd39fad92719d0fb3985b251da5802df13aaa09f567"

