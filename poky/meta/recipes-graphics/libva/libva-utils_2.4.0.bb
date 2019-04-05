SUMMARY = "libva-utils is a collection of utilities from libva project"

DESCRIPTION = "libva-utils is a collection of utilities \
and examples to exercise VA-API in accordance with the libva \
project.VA-API is an open-source library and API specification, \
which provides access to graphics hardware acceleration capabilities \
for video processing. It consists of a main library and driver-specific \
acceleration backends for each supported hardware vendor"

HOMEPAGE = "https://01.org/linuxmedia/vaapi"
BUGTRACKER = "https://github.com/intel/libva-utils/issues"

SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b148fc8adf19dc9aec17cf9cd29a9a5e"

SRC_URI = "https://github.com/intel/${BPN}/releases/download/${PV}/${BP}.tar.bz2 \
           file://0001-Build-sfcsample-only-when-X11-backend-is-enabled.patch \
           "

SRC_URI[md5sum] = "f5374c4c32ce136e50aea0267887aed5"
SRC_URI[sha256sum] = "5b7d1954b40fcb2c0544be20125c71a0852049715ab85a3e8aba60434a40c6b3"

UPSTREAM_CHECK_URI = "https://github.com/intel/libva-utils/releases"

DEPENDS = "libva"

inherit autotools pkgconfig distro_features_check

# depends on libva which requires opengl
REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11 libxext libxfixes"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland-native wayland"
