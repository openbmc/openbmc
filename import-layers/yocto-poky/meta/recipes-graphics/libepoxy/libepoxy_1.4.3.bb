SUMMARY = "OpenGL function pointer management library"
HOMEPAGE = "https://github.com/anholt/libepoxy/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=58ef4c80d401e07bd9ee8b6b58cf464b"

SRC_URI = "https://github.com/anholt/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://Add-fallback-definition-for-EGL-CAST.patch"
SRC_URI[md5sum] = "af4c3ce0fb1143bdc4e43f85695a9bed"
SRC_URI[sha256sum] = "0b808a06c9685a62fca34b680abb8bc7fb2fda074478e329b063c1f872b826f6"
UPSTREAM_CHECK_URI = "https://github.com/anholt/libepoxy/releases"

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "util-macros"

PACKAGECONFIG[egl] = "--enable-egl, --disable-egl, virtual/egl"
PACKAGECONFIG[x11] = "--enable-glx, --disable-glx, virtual/libx11"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} egl"
