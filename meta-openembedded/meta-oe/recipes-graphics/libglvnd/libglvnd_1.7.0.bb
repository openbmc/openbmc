DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating \
OpenGL API calls between multiple vendors."
HOMEPAGE = "https://gitlab.freedesktop.org/glvnd/libglvnd"
LICENSE = "MIT & BSD-1-Clause & BSD-3-Clause & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=323;md5=7ac5f0111f648b92fe5427efeb08e8c4"

SRC_URI = "git://git@gitlab.freedesktop.org/glvnd/libglvnd.git;protocol=https;branch=master"

# v1.5.0 tag
SRCREV = "faa23f21fc677af5792825dc30cb1ccef4bf33a6"

REQUIRED_DISTRO_FEATURES = "opengl"

inherit meson pkgconfig features_check

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "\
  ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'egl gles1 gles2', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'glx', '', d)} \
  "

PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,libx11 libxext xorgproto"
PACKAGECONFIG[glx] = "-Dglx=enabled,-Dglx=disabled,libx11 libxext xorgproto"
PACKAGECONFIG[egl] = "-Degl=true,-Degl=false,"
PACKAGECONFIG[gles1] = "-Dgles1=true,-Dgles1=false,"
PACKAGECONFIG[gles2] = "-Dgles2=true,-Dgles2=false,"

BBCLASSEXTEND = "native nativesdk"
