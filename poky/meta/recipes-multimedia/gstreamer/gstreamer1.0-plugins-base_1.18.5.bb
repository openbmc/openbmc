require gstreamer1.0-plugins-common.inc

DESCRIPTION = "'Base' GStreamer plugins and helper libraries"
HOMEPAGE = "https://gstreamer.freedesktop.org/"
BUGTRACKER = "https://gitlab.freedesktop.org/gstreamer/gst-plugins-base/-/issues"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d"

SRC_URI = "https://gstreamer.freedesktop.org/src/gst-plugins-base/gst-plugins-base-${PV}.tar.xz \
           file://0001-ENGR00312515-get-caps-from-src-pad-when-query-caps.patch \
           file://0003-viv-fb-Make-sure-config.h-is-included.patch \
           file://0002-ssaparse-enhance-SSA-text-lines-parsing.patch \
           file://0004-glimagesink-Downrank-to-marginal.patch \
           "
SRC_URI[sha256sum] = "960b7af4585700db0fdd5b843554e11e2564fed9e061f591fae88a7be6446fa3"

S = "${WORKDIR}/gst-plugins-base-${PV}"

DEPENDS += "iso-codes util-linux zlib"

inherit gobject-introspection

PACKAGES_DYNAMIC =+ "^libgst.*"

# opengl packageconfig factored out to make it easy for distros
# and BSP layers to choose OpenGL APIs/platforms/window systems
PACKAGECONFIG_GL ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2 egl', '', d)}"

PACKAGECONFIG ??= " \
    ${GSTREAMER_ORC} \
    ${PACKAGECONFIG_GL} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa x11', d)} \
    jpeg ogg pango png theora vorbis \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland egl', '', d)} \
"

OPENGL_APIS = 'opengl gles2'
OPENGL_PLATFORMS = 'egl'

X11DEPENDS = "virtual/libx11 libsm libxrender libxv"
X11ENABLEOPTS = "-Dx11=enabled -Dxvideo=enabled -Dxshm=enabled"
X11DISABLEOPTS = "-Dx11=disabled -Dxvideo=disabled -Dxshm=disabled"

PACKAGECONFIG[alsa]         = "-Dalsa=enabled,-Dalsa=disabled,alsa-lib"
PACKAGECONFIG[cdparanoia]   = "-Dcdparanoia=enabled,-Dcdparanoia=disabled,cdparanoia"
PACKAGECONFIG[graphene]     = "-Dgl-graphene=enabled,-Dgl-graphene=disabled,graphene"
PACKAGECONFIG[jpeg]         = "-Dgl-jpeg=enabled,-Dgl-jpeg=disabled,jpeg"
PACKAGECONFIG[ogg]          = "-Dogg=enabled,-Dogg=disabled,libogg"
PACKAGECONFIG[opus]         = "-Dopus=enabled,-Dopus=disabled,libopus"
PACKAGECONFIG[pango]        = "-Dpango=enabled,-Dpango=disabled,pango"
PACKAGECONFIG[png]          = "-Dgl-png=enabled,-Dgl-png=disabled,libpng"
PACKAGECONFIG[theora]       = "-Dtheora=enabled,-Dtheora=disabled,libtheora"
PACKAGECONFIG[tremor]       = "-Dtremor=enabled,-Dtremor=disabled,tremor"
PACKAGECONFIG[visual]       = "-Dlibvisual=enabled,-Dlibvisual=disabled,libvisual"
PACKAGECONFIG[vorbis]       = "-Dvorbis=enabled,-Dvorbis=disabled,libvorbis"
PACKAGECONFIG[x11]          = "${X11ENABLEOPTS},${X11DISABLEOPTS},${X11DEPENDS}"

# OpenGL API packageconfigs
PACKAGECONFIG[opengl]       = ",,virtual/libgl libglu"
PACKAGECONFIG[gles2]        = ",,virtual/libgles2"

# OpenGL platform packageconfigs
PACKAGECONFIG[egl]          = ",,virtual/egl"

# OpenGL window systems (except for X11)
PACKAGECONFIG[gbm]          = ",,virtual/libgbm libgudev libdrm"
PACKAGECONFIG[wayland]      = ",,wayland-native wayland wayland-protocols libdrm"
PACKAGECONFIG[dispmanx]     = ",,virtual/libomxil"
PACKAGECONFIG[viv-fb]       = ",,virtual/libgles2 virtual/libg2d"

OPENGL_WINSYS = "${@bb.utils.filter('PACKAGECONFIG', 'x11 gbm wayland dispmanx egl viv-fb', d)}"

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    ${@get_opengl_cmdline_list('gl_api', d.getVar('OPENGL_APIS'), d)} \
    ${@get_opengl_cmdline_list('gl_platform', d.getVar('OPENGL_PLATFORMS'), d)} \
    ${@get_opengl_cmdline_list('gl_winsys', d.getVar('OPENGL_WINSYS'), d)} \
"

FILES:${PN}-dev += "${libdir}/gstreamer-1.0/include/gst/gl/gstglconfig.h"
FILES:${MLPREFIX}libgsttag-1.0 += "${datadir}/gst-plugins-base/1.0/license-translations.dict"

def get_opengl_cmdline_list(switch_name, options, d):
    selected_options = []
    if bb.utils.contains('DISTRO_FEATURES', 'opengl', True, False, d):
        for option in options.split():
            if bb.utils.contains('PACKAGECONFIG', option, True, False, d):
                selected_options += [option]
    if selected_options:
        return '-D' + switch_name + '=' + ','.join(selected_options)
    else:
        return ''

CVE_PRODUCT += "gst-plugins-base"
