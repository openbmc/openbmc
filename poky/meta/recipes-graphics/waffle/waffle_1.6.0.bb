SUMMARY = "cross-platform C library to defer selection of GL API and of window system"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4c5154407c2490750dd461c50ad94797 \
                    file://include/waffle/waffle.h;endline=24;md5=61dbf8697f61c78645e75a93c585b1bf"

SRC_URI = "http://waffle-gl.org/files/release/${BPN}-${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "61bfc1a478e840825f33ddb4057115e7"
SRC_URI[sha256sum] = "d9c899f710c50cfdd00f5f4cdfeaef0687d8497362239bdde93bed6c909c81d7"

UPSTREAM_CHECK_URI = "http://www.waffle-gl.org/releases.html"

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
