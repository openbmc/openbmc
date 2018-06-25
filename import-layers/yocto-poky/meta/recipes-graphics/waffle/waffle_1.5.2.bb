SUMMARY = "cross-platform C library to defer selection of GL API and of window system"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4c5154407c2490750dd461c50ad94797 \
                    file://include/waffle/waffle.h;endline=24;md5=61dbf8697f61c78645e75a93c585b1bf"

SRC_URI = "http://waffle-gl.org/files/release/${BPN}-${PV}/${BPN}-${PV}.tar.xz \
           file://0001-third_party-threads-Use-PTHREAD_MUTEX_RECURSIVE-by-d.patch \
          "
SRC_URI[md5sum] = "c669c91bf2f7e13a5d781c3dbb30fd8c"
SRC_URI[sha256sum] = "d2c096cf654bf0061323a4b9231a1ef5b749a1e5c7c5bfe067e964219c2a851c"

UPSTREAM_CHECK_URI = "http://www.waffle-gl.org/releases.html"

inherit cmake distro_features_check lib_package

# This should be overridden per-machine to reflect the capabilities of the GL
# stack.
PACKAGECONFIG ??= "glx"

# libx11 requires x11 in DISTRO_FEATURES.
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'glx', 'x11', '', d)}"

# virtual/libgl requires opengl in DISTRO_FEATURES.
REQUIRED_DISTRO_FEATURES += "${@bb.utils.contains('DEPENDS', 'virtual/${MLPREFIX}libgl', 'opengl', '', d)}"

# I say virtual/libgl, actually wants gl.pc
PACKAGECONFIG[glx] = "-Dwaffle_has_glx=1,-Dwaffle_has_glx=0,virtual/${MLPREFIX}libgl libx11"

# I say virtual/libgl, actually wants wayland-egl.pc, egl.pc, and the wayland
# DISTRO_FEATURE.
PACKAGECONFIG[wayland] = "-Dwaffle_has_wayland=1,-Dwaffle_has_wayland=0,virtual/${MLPREFIX}libgl wayland"

# I say virtual/libgl, actually wants gbm.pc egl.pc
PACKAGECONFIG[gbm] = "-Dwaffle_has_gbm=1,-Dwaffle_has_gbm=0,virtual/${MLPREFIX}libgl udev"

# I say virtual/libgl, actually wants egl.pc
PACKAGECONFIG[x11-egl] = "-Dwaffle_has_x11_egl=1,-Dwaffle_has_x11_egl=0,virtual/${MLPREFIX}libgl libxcb"
