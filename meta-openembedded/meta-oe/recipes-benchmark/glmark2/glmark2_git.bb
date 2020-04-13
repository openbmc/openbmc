SUMMARY = "OpenGL (ES) 2.0 benchmark"
DESCRIPTION = "glmark2 is a benchmark for OpenGL (ES) 2.0. \
It uses only the subset of the OpenGL 2.0 API that is compatible with OpenGL ES 2.0."
HOMEPAGE = "https://launchpad.net/glmark2"
BUGTRACKER = "https://bugs.launchpad.net/glmark2"

LICENSE = "GPLv3+ & SGIv1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.SGI;beginline=5;md5=269cdab4af6748677acce51d9aa13552"

DEPENDS = "libpng jpeg udev"

PV = "20191226+${SRCPV}"

COMPATIBLE_HOST_rpi  = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '.*-linux*', 'null', d)}"

SRC_URI = "git://github.com/glmark2/glmark2.git;protocol=https \
           file://python3.patch"
SRCREV = "72dabc5d72b49c6d45badeb8a941ba4d829b0bd6"

S = "${WORKDIR}/git"

inherit waf pkgconfig features_check

REQUIRED_DISTRO_FEATURES += "opengl"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'x11-gl x11-gles2', '', d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'wayland opengl', 'wayland-gl wayland-gles2', '', d)} \
                  drm-gl drm-gles2"

PACKAGECONFIG[x11-gl] = ",,virtual/libgl virtual/libx11"
PACKAGECONFIG[x11-gles2] = ",,virtual/libgles2 virtual/libx11"
PACKAGECONFIG[drm-gl] = ",,virtual/libgl libdrm virtual/libgbm"
PACKAGECONFIG[drm-gles2] = ",,virtual/libgles2 libdrm virtual/libgbm"
PACKAGECONFIG[wayland-gl] = ",,virtual/libgl wayland"
PACKAGECONFIG[wayland-gles2] = ",,virtual/libgles2 wayland"

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
    if flavors:
        d.appendVar("EXTRA_OECONF", " --with-flavors=%s" % ",".join(flavors))
}

