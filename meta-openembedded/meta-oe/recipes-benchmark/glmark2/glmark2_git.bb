SUMMARY = "OpenGL (ES) 2.0 benchmark"
DESCRIPTION = "glmark2 is a benchmark for OpenGL (ES) 2.0. \
It uses only the subset of the OpenGL 2.0 API that is compatible with OpenGL ES 2.0."
HOMEPAGE = "https://github.com/glmark2/glmark2"
BUGTRACKER = "https://github.com/glmark2/glmark2/issues"

LICENSE = "GPLv3+ & SGIv1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.SGI;beginline=5;md5=269cdab4af6748677acce51d9aa13552"

DEPENDS = "libpng jpeg udev"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-native', '', d)}"

PV = "20201114+${SRCPV}"

SRC_URI = " \
    git://github.com/glmark2/glmark2.git;protocol=https \
    file://0001-fix-dispmanx-build.patch \
    file://0002-run-dispmanx-fullscreen.patch \
    file://0003-fix-precision-handling-bugs.patch \
    "
SRCREV = "784aca755a469b144acf3cae180b6e613b7b057a"

S = "${WORKDIR}/git"

inherit waf pkgconfig features_check

ANY_OF_DISTRO_FEATURES = "opengl dispmanx"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'x11-gles2', '', d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'wayland opengl', 'wayland-gles2', '', d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'dispmanx', 'dispmanx', '', d)} \
                  drm-gles2 \
                 "

PACKAGECONFIG[x11-gl] = ",,virtual/libgl virtual/libx11"
PACKAGECONFIG[x11-gles2] = ",,virtual/libgles2 virtual/libx11"
PACKAGECONFIG[drm-gl] = ",,virtual/libgl libdrm virtual/libgbm"
PACKAGECONFIG[drm-gles2] = ",,virtual/libgles2 libdrm virtual/libgbm"
PACKAGECONFIG[wayland-gl] = ",,virtual/libgl wayland"
PACKAGECONFIG[wayland-gles2] = ",,virtual/libgles2 wayland"
PACKAGECONFIG[dispmanx] = ",,virtual/libgles2 virtual/libx11"

python __anonymous() {
    packageconfig = (d.getVar("PACKAGECONFIG") or "").split()
    flavors = []
    if "x11-gles2" in packageconfig:
        flavors.append("x11-glesv2")
    if "x11-gl" in packageconfig:
        flavors.append("x11-gl")
    if "wayland-gles2" in packageconfig:
        flavors.append("wayland-glesv2")
    if "wayland-gl" in packageconfig:
        flavors.append("wayland-gl")
    if "drm-gles2" in packageconfig:
        flavors.append("drm-glesv2")
    if "drm-gl" in packageconfig:
        flavors.append("drm-gl")
    if "dispmanx" in packageconfig:
        flavors = ["dispmanx-glesv2"]
    if flavors:
        d.appendVar("EXTRA_OECONF", " --with-flavors=%s" % ",".join(flavors))
}

