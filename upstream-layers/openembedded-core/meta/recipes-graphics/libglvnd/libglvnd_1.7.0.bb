SUMMARY = "the GL Vendor-Neutral Dispatch library"
DESCRIPTION = "libglvnd is a vendor-neutral dispatch layer for arbitrating \
OpenGL API calls between multiple vendors."
HOMEPAGE = "https://gitlab.freedesktop.org/glvnd/libglvnd"
LICENSE = "MIT & BSD-1-Clause & BSD-3-Clause & GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://README.md;beginline=323;md5=7ac5f0111f648b92fe5427efeb08e8c4"

SRC_URI = "git://gitlab.freedesktop.org/glvnd/libglvnd.git;protocol=https;branch=master"

SRCREV = "faa23f21fc677af5792825dc30cb1ccef4bf33a6"

REQUIRED_DISTRO_FEATURES = "opengl glvnd"

inherit meson pkgconfig features_check

PACKAGECONFIG ?= "\
  ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'egl gles1 gles2', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'glx', '', d)} \
  "

PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,libx11 libxext xorgproto"
PACKAGECONFIG[glx] = "-Dglx=enabled,-Dglx=disabled,libx11 libxext xorgproto,,virtual-libglx-icd"
PACKAGECONFIG[egl] = "-Degl=true,-Degl=false,,virtual-libegl-icd"
PACKAGECONFIG[gles1] = "-Dgles1=true,-Dgles1=false,"
PACKAGECONFIG[gles2] = "-Dgles2=true,-Dgles2=false,"

BBCLASSEXTEND = "native nativesdk"

PROVIDES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'glx', 'virtual/libgl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gles1', 'virtual/libgles1', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gles2', 'virtual/libgles2 virtual/libgles3', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'virtual/egl', '', d)} \
"

RPROVIDES:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'egl', 'libegl libegl1', '', d)}"
RPROVIDES:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'glx', 'libgl libgl1', '', d)}"
RPROVIDES:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'gles1', 'libgles1 libglesv1-cm1', '', d)}"
RPROVIDES:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'gles2', 'libgles2 libglesv2-2 libgles3', '', d)}"

RPROVIDES:${PN}-dev += "${@bb.utils.contains('PACKAGECONFIG', 'egl', 'libegl-dev', '', d)}"
RPROVIDES:${PN}-dev += "${@bb.utils.contains('PACKAGECONFIG', 'glx', 'libgl-dev', '', d)}"
RPROVIDES:${PN}-dev += "${@bb.utils.contains('PACKAGECONFIG', 'gles1', 'libgles1-dev', '', d)}"
RPROVIDES:${PN}-dev += "${@bb.utils.contains('PACKAGECONFIG', 'gles2', 'libgles2-dev libgles3-dev', '', d)}"
