SUMMARY = "Wayland utilities"
DESCRIPTION = "Wayland-utils contains (for now) \
wayland-info, a utility for displaying information about the Wayland \
protocols supported by a Wayland compositor. \
wayland-info is basically a standalone version of weston-info as found \
in weston repository. "
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=548a66038a77415e1df51118625e832f \
                   "

SRC_URI = "https://gitlab.freedesktop.org/wayland/wayland-utils/-/releases/${PV}/downloads/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "d9278c22554586881802540751bcc42569262bf80cd9ac9b0fd12ff4bd09a9e4"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/wayland/wayland-utils/-/tags"

inherit meson pkgconfig

DEPENDS += "wayland wayland-native wayland-protocols"

PACKAGECONFIG ??= "drm"
PACKAGECONFIG[drm] = "-Ddrm=enabled,-Ddrm=disabled,libdrm"
