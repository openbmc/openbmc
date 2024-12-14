SUMMARY = "Demo application to showcase 3D graphics using kms and gbm"
DESCRIPTION = "kmscube is a little demonstration program for how to drive bare metal graphics \
without a compositor like X11, wayland or similar, using DRM/KMS (kernel mode \
setting), GBM (graphics buffer manager) and EGL for rendering content using \
OpenGL or OpenGL ES."
HOMEPAGE = "https://cgit.freedesktop.org/mesa/kmscube/"
LICENSE = "MIT"
SECTION = "graphics"
DEPENDS = "virtual/libgles3 virtual/libgles2 virtual/egl libdrm virtual/libgbm"

LIC_FILES_CHKSUM = "file://COPYING;md5=2a12bf7a66f5f663d75186bf01eb607b"

SRCREV = "311eaaaa473d593c30d118799aa19ac4ad53cd65"
SRC_URI = "git://gitlab.freedesktop.org/mesa/kmscube;branch=master;protocol=https \
           "

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

BASEPV = "0.0.1"
PV = "${BASEPV}+git"

inherit meson pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"

CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '-DEGL_NO_X11=1', d)}"

