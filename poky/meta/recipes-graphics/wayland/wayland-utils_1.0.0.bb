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

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           "
SRC_URI[sha256sum] = "64fecc4c58e87ae9b302901abe10c2e8af69c7503c221a96ecd0700e0aa268c0"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit meson pkgconfig

DEPENDS += "wayland wayland-native wayland-protocols"
