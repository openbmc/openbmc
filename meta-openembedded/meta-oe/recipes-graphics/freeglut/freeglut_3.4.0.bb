DESCRIPTION = "FreeGLUT is a free-software/open-source alternative to the OpenGL \
               Utility Toolkit (GLUT) library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=89c0b58a3e01ce3d8254c9f59e78adfb"

SRC_URI = "https://sourceforge.net/projects/${BPN}/files/${BPN}/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "3c0bcb915d9b180a97edaebd011b7a1de54583a838644dcd42bb0ea0c6f3eaec"

inherit cmake features_check

# depends on virtual/libx11, virtual/libgl
REQUIRED_DISTRO_FEATURES = "x11 opengl"

# Do not use -fno-common, check back when upgrading to new version it might not be needed
CFLAGS += "-fcommon"

PROVIDES += "mesa-glut"

DEPENDS = "virtual/libx11 libxmu libxi virtual/libgl libglu libxrandr"
