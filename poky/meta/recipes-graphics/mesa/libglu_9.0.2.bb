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

SRC_URI = "https://mesa.freedesktop.org/archive/glu/glu-${PV}.tar.gz"

SRC_URI[sha256sum] = "24effdfb952453cc00e275e1c82ca9787506aba0282145fff054498e60e19a65"

S = "${WORKDIR}/glu-${PV}"

DEPENDS = "virtual/libgl"

inherit autotools pkgconfig features_check

# Requires libGL.so which is provided by mesa when x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11 opengl"

# Remove the mesa-glu dependency in mesa-glu-dev, as mesa-glu is empty
RDEPENDS:${PN}-dev = ""
