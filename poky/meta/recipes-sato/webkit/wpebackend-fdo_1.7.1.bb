SUMMARY = "WPE's backend based on a freedesktop.org stack."
HOMEPAGE = "https://github.com/Igalia/WPEBackend-fdo"
BUGTRACKER = "https://github.com/Igalia/WPEBackend-fdo/issues"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f62cef2e3645e3e74eb05fd389d7a66"
DEPENDS = "glib-2.0 libxkbcommon wayland virtual/egl libwpe libepoxy"

DEPENDS_append_class-target = " wayland-native"

inherit meson features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "https://wpewebkit.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "9b980a73ea4e3762266c48f81ded56d9dcad4acf32bad9bd05d0dffdd454c6f5"

