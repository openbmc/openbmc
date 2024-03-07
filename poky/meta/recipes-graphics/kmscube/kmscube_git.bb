SUMMARY = "Demo application to showcase 3D graphics using kms and gbm"
DESCRIPTION = "kmscube is a little demonstration program for how to drive bare metal graphics \
without a compositor like X11, wayland or similar, using DRM/KMS (kernel mode \
setting), GBM (graphics buffer manager) and EGL for rendering content using \
OpenGL or OpenGL ES."
HOMEPAGE = "https://cgit.freedesktop.org/mesa/kmscube/"
LICENSE = "MIT"
SECTION = "graphics"
DEPENDS = "virtual/libgles3 virtual/libgles2 virtual/egl libdrm virtual/libgbm"

LIC_FILES_CHKSUM = "file://kmscube.c;beginline=1;endline=23;md5=8b309d4ee67b7315ff7381270dd631fb"

SRCREV = "6ab022fdfcfedd28f4b5dbd8d3299414a367746f"
SRC_URI = "git://gitlab.freedesktop.org/mesa/kmscube;branch=master;protocol=https \
           file://0001-cube-gears-Change-header-file-to-GLES3-gl3.h.patch \
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

