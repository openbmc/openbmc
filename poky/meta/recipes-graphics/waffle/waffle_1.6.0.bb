SUMMARY = "A C library for selecting an OpenGL API and window system at runtime"
DESCRIPTION = "A cross-platform C library that allows one to defer selection \
of an OpenGL API and window system until runtime. For example, on Linux, Waffle \
enables an application to select X11/EGL with an OpenGL 3.3 core profile, \
Wayland with OpenGL ES2, and other window system / API combinations."
HOMEPAGE = "https://gitlab.freedesktop.org/mesa/waffle"
BUGTRACKER = "https://gitlab.freedesktop.org/mesa/waffle"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4c5154407c2490750dd461c50ad94797 \
                    file://include/waffle/waffle.h;endline=24;md5=61dbf8697f61c78645e75a93c585b1bf"

SRC_URI = "https://gitlab.freedesktop.org/mesa/waffle/-/archive/v${PV}/${BPN}-v${PV}.tar.bz2"
SRC_URI[md5sum] = "9eaef03c8220dc8d64e2e42ae1b8c942"
SRC_URI[sha256sum] = "38ef38fefbda605ba905ce00435a63fe45e9bf17a5eff096c3a47b5006a619cb"

S = "${WORKDIR}/${BPN}-v${PV}"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/mesa/waffle/-/releases"

inherit meson features_check lib_package bash-completion

DEPENDS_append = " python3"

# This should be overridden per-machine to reflect the capabilities of the GL
# stack.
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'glx x11-egl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
                   surfaceless-egl gbm"

# virtual/libgl requires opengl in DISTRO_FEATURES.
REQUIRED_DISTRO_FEATURES += "${@bb.utils.contains('DEPENDS', 'virtual/${MLPREFIX}libgl', 'opengl', '', d)}"

# I say virtual/libgl, actually wants gl.pc
PACKAGECONFIG[glx] = "-Dglx=enabled,-Dglx=disabled,virtual/${MLPREFIX}libgl libx11"

# I say virtual/libgl, actually wants wayland-egl.pc, egl.pc, and the wayland
# DISTRO_FEATURE.
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,virtual/${MLPREFIX}libgl wayland"

# I say virtual/libgl, actually wants gbm.pc egl.pc
PACKAGECONFIG[gbm] = "-Dgbm=enabled,-Dgbm=disabled,virtual/${MLPREFIX}libgl udev"

# I say virtual/libgl, actually wants egl.pc
PACKAGECONFIG[x11-egl] = "-Dx11_egl=enabled,-Dx11_egl=disabled,virtual/${MLPREFIX}libgl libxcb"
PACKAGECONFIG[surfaceless-egl] = "-Dsurfaceless_egl=enabled,-Dsurfaceless_egl=disabled,virtual/${MLPREFIX}libgl"

# TODO: optionally build manpages and examples
