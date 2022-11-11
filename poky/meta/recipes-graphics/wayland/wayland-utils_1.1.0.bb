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

SRC_URI = "https://gitlab.freedesktop.org/wayland/wayland-utils/-/releases/${PV}/downloads/${BPN}-${PV}.tar.xz \
           file://0001-wayland-info-Fix-build-without-libdrm.patch \
           "
SRC_URI[sha256sum] = "9e685863025b4feade36d53bbc8e31b43e26498be743dea84c7a84912959410a"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit meson pkgconfig

DEPENDS += "wayland wayland-native wayland-protocols"
