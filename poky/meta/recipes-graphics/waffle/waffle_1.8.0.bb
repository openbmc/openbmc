SUMMARY = "A C library for selecting an OpenGL API and window system at runtime"
DESCRIPTION = "A cross-platform C library that allows one to defer selection \
of an OpenGL API and window system until runtime. For example, on Linux, Waffle \
enables an application to select X11/EGL with an OpenGL 3.3 core profile, \
Wayland with OpenGL ES2, and other window system / API combinations."
HOMEPAGE = "https://gitlab.freedesktop.org/mesa/waffle"
BUGTRACKER = "https://gitlab.freedesktop.org/mesa/waffle"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4c5154407c2490750dd461c50ad94797 \
                    file://include/waffle-1/waffle.h;endline=24;md5=61dbf8697f61c78645e75a93c585b1bf"

SRC_URI = "git://gitlab.freedesktop.org/mesa/waffle.git;protocol=https;branch=master \
           file://0001-waffle-do-not-make-core-protocol-into-the-library.patch \
           "
SRCREV = "580b912a30085528886603942c100c7b309b3bdb"
S = "${WORKDIR}/git"

inherit meson features_check lib_package bash-completion pkgconfig

DEPENDS:append = " python3"

# This should be overridden per-machine to reflect the capabilities of the GL
# stack.
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'glx x11-egl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gbm surfaceless-egl', '', d)} \
"

# virtual/libgl requires opengl in DISTRO_FEATURES.
REQUIRED_DISTRO_FEATURES += "${@bb.utils.contains('DEPENDS', 'virtual/${MLPREFIX}libgl', 'opengl', '', d)}"

# I say virtual/libgl, actually wants gl.pc
PACKAGECONFIG[glx] = "-Dglx=enabled,-Dglx=disabled,virtual/${MLPREFIX}libgl libx11"

# wants wayland-egl.pc, egl.pc, and the wayland
# DISTRO_FEATURE.
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,virtual/${MLPREFIX}egl wayland wayland-native wayland-protocols"

# wants gbm.pc egl.pc
PACKAGECONFIG[gbm] = "-Dgbm=enabled,-Dgbm=disabled,virtual/${MLPREFIX}egl virtual/${MLPREFIX}libgbm udev"

# wants egl.pc
PACKAGECONFIG[x11-egl] = "-Dx11_egl=enabled,-Dx11_egl=disabled,virtual/${MLPREFIX}egl libxcb"
PACKAGECONFIG[surfaceless-egl] = "-Dsurfaceless_egl=enabled,-Dsurfaceless_egl=disabled,virtual/${MLPREFIX}egl"

# TODO: optionally build manpages and examples

do_install:append() {
    rm -rf ${D}${datadir}/zsh
}
