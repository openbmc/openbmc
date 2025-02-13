SUMMARY = "General-purpose library specifically developed for the WPE-flavored port of WebKit."
HOMEPAGE = "https://github.com/WebPlatformForEmbedded/libwpe"
BUGTRACKER = "https://github.com/WebPlatformForEmbedded/libwpe/issues"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=371a616eb4903c6cb79e9893a5f615cc"
DEPENDS = "virtual/egl libxkbcommon"

inherit cmake features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "https://wpewebkit.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "9cca60f2c4393ea0de53c675ebc3cdbe9c5aa249259dd1d6d81a49b052d37481"

# This is a tweak of upstream-version-is-even needed because
# ipstream directory contains tarballs for other components as well.
UPSTREAM_CHECK_REGEX = "libwpe-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)\.tar"
