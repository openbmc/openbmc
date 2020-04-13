SUMMARY = "VA-API support to GStreamer"
DESCRIPTION = "gstreamer-vaapi consists of a collection of VA-API \
based plugins for GStreamer and helper libraries: `vaapidecode', \
`vaapiconvert', and `vaapisink'."

REALPN = "gstreamer-vaapi"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://gstreamer.freedesktop.org/src/${REALPN}/${REALPN}-${PV}.tar.xz \
           file://0001-vaapsink-downgrade-to-marginal.patch \
           "

SRC_URI[md5sum] = "13f7cb6a64bde24e67f563377487dcce"
SRC_URI[sha256sum] = "191de7b0ab64a85dd0875c990721e7be95518f60e2a9106beca162004ed7c601"

S = "${WORKDIR}/${REALPN}-${PV}"
DEPENDS = "libva gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad"

inherit meson pkgconfig gtk-doc features_check upstream-version-is-even

REQUIRED_DISTRO_FEATURES ?= "opengl"

EXTRA_OEMESON += " \
    -Dexamples=disabled \
"

GTKDOC_MESON_OPTION = "gtk_doc"
GTKDOC_MESON_ENABLE_FLAG = "enabled"
GTKDOC_MESON_DISABLE_FLAG = "disabled"

PACKAGES =+ "${PN}-tests"

# OpenGL packageconfig factored out to make it easy for distros
# and BSP layers to pick either glx, egl, or no GL. By default,
# try detecting X11 first, and if found (with OpenGL), use GLX,
# otherwise try to check if EGL can be used.
PACKAGECONFIG_GL ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'glx', \
                        bb.utils.contains('DISTRO_FEATURES',     'opengl', 'egl', \
                                                                       '', d), d)}"

PACKAGECONFIG ??= "drm encoders \
                   ${PACKAGECONFIG_GL} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"

PACKAGECONFIG[drm] = "-Dwith_drm=yes,-Dwith_drm=no,udev libdrm"
PACKAGECONFIG[egl] = "-Dwith_egl=yes,-Dwith_egl=no,virtual/egl"
PACKAGECONFIG[encoders] = "-Dwith_encoders=yes,-Dwith_encoders=no"
PACKAGECONFIG[glx] = "-Dwith_glx=yes,-Dwith_glx=no,virtual/libgl"
PACKAGECONFIG[wayland] = "-Dwith_wayland=yes,-Dwith_wayland=no,wayland-native wayland wayland-protocols"
PACKAGECONFIG[x11] = "-Dwith_x11=yes,-Dwith_x11=no,virtual/libx11 libxrandr libxrender"

FILES_${PN} += "${libdir}/gstreamer-*/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-*/.debug"
FILES_${PN}-dev += "${libdir}/gstreamer-*/*.a"
FILES_${PN}-tests = "${bindir}/*"
