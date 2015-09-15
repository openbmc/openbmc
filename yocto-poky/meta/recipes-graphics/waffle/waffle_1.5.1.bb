SUMMARY = "cross-platform C library to defer selection of GL API and of window system"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4c5154407c2490750dd461c50ad94797 \
                    file://include/waffle/waffle.h;endline=24;md5=61dbf8697f61c78645e75a93c585b1bf"

SRC_URI = "http://waffle-gl.org/files/release/${BPN}-${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "c0d802bc3d0aba87c51e423a3a8bdd69"
SRC_URI[sha256sum] = "cbab0e926515064e818bf089a5af04be33307e5f40d07659fb40d59b2bfe20aa"

inherit cmake distro_features_check lib_package

# This should be overridden per-machine to reflect the capabilities of the GL
# stack.
PACKAGECONFIG ??= "glx"

# libx11 requires x11 in DISTRO_FEATURES.
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'glx', 'x11', '', d)}"

# I say virtual/libgl, actually wants gl.pc
PACKAGECONFIG[glx] = "-Dwaffle_has_glx=1,-Dwaffle_has_glx=0,virtual/libgl libx11"

# I say virtual/libgl, actually wants wayland-egl.pc, egl.pc, and the wayland
# DISTRO_FEATURE.
PACKAGECONFIG[wayland] = "-Dwaffle_has_wayland=1,-Dwaffle_has_wayland=0,virtual/libgl wayland"

# I say virtual/libgl, actually wants gbm.pc egl.pc
PACKAGECONFIG[gbm] = "-Dwaffle_has_gbm=1,-Dwaffle_has_gbm=0,virtual/libgl udev"

# I say virtual/libgl, actually wants egl.pc
PACKAGECONFIG[x11-egl] = "-Dwaffle_has_x11_egl=1,-Dwaffle_has_x11_egl=0,virtual/libgl libxcb"

FILES_${PN}-dev += "${datadir}/cmake/Modules/FindWaffle.cmake \
                    ${libdir}/cmake/Waffle/"
