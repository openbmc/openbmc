SUMMARY = "The OpenGL utility toolkit"
DESCRIPTION = "GLU is a utility toolkit used with OpenGL implementations"

HOMEPAGE = "http://mesa3d.org"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://include/GL/glu.h;endline=29;md5=6b79c570f644363b356456e7d44471d9 \
                    file://src/libtess/tess.c;endline=29;md5=6b79c570f644363b356456e7d44471d9"

# Epoch as this used to be part of mesa
PE = "2"

SRC_URI = "https://mesa.freedesktop.org/archive/glu/glu-${PV}.tar.xz \
           "

SRC_URI[sha256sum] = "bd43fe12f374b1192eb15fe20e45ff456b9bc26ab57f0eee919f96ca0f8a330f"

S = "${WORKDIR}/glu-${PV}"

DEPENDS = "virtual/libgl"

inherit meson pkgconfig features_check

EXTRA_OEMESON = "-Dgl_provider=gl"

# Requires libGL.so which is provided by mesa when x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11 opengl"

# Remove the mesa-glu dependency in mesa-glu-dev, as mesa-glu is empty
DEV_PKG_DEPENDENCY = ""
