DESCRIPTION = "FreeGLUT is a free-software/open-source alternative to the OpenGL \
               Utility Toolkit (GLUT) library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=89c0b58a3e01ce3d8254c9f59e78adfb"

SRC_URI = "https://sourceforge.net/projects/${BPN}/files/${BPN}/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "cd5c670c1086358598a6d4a9d166949d"
SRC_URI[sha256sum] = "d4000e02102acaf259998c870e25214739d1f16f67f99cb35e4f46841399da68"

inherit cmake features_check

# depends on virtual/libx11, virtual/libgl
REQUIRED_DISTRO_FEATURES = "x11 opengl"

# Do not use -fno-common, check back when upgrading to new version it might not be needed
CFLAGS += "-fcommon"

PROVIDES += "mesa-glut"

DEPENDS = "virtual/libx11 libxmu libxi virtual/libgl libglu libxrandr"
