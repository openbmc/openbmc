SUMMARY = "OpenGL function pointer management library"
HOMEPAGE = "https://github.com/anholt/libepoxy/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=58ef4c80d401e07bd9ee8b6b58cf464b"

SRC_URI = "https://github.com/anholt/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           "
SRC_URI[md5sum] = "4a6b9e581da229dee74c2263c84b1eca"
SRC_URI[sha256sum] = "a9562386519eb3fd7f03209f279f697a8cba520d3c155d6e253c3e138beca7d8"
UPSTREAM_CHECK_URI = "https://github.com/anholt/libepoxy/releases"

inherit meson pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG[egl] = "-Degl=yes, -Degl=no, virtual/egl"
PACKAGECONFIG[x11] = "-Dglx=yes, -Dglx=no, virtual/libx11 virtual/libgl"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} egl"

EXTRA_OEMESON += "-Dtests=false"
