SUMMARY = "WPE's backend based on a freedesktop.org stack."
HOMEPAGE = "https://github.com/Igalia/WPEBackend-fdo"
BUGTRACKER = "https://github.com/Igalia/WPEBackend-fdo/issues"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f62cef2e3645e3e74eb05fd389d7a66"
DEPENDS = "glib-2.0 libxkbcommon wayland virtual/egl libwpe libepoxy"

DEPENDS:append:class-target = " wayland-native"

inherit meson features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "https://wpewebkit.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "6239c9c15523410798d66315de6b491712ab30009ba180f3e0dd076d9b0074ac"

# Especially helps compiling with clang which enable this as error when
# using c++11
CXXFLAGS += "-Wno-c++11-narrowing"

# This is a tweak of upstream-version-is-even needed because
# ipstream directory contains tarballs for other components as well.
UPSTREAM_CHECK_REGEX = "wpebackend-fdo-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)\.tar"
