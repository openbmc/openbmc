SUMMARY = "Modern 3D graphics API with associated utility APIs"
HOMEPAGE = "http://wiki.clutter-project.org/wiki/Cogl"
LICENSE = "MIT"

inherit clutter distro_features_check upstream-version-is-even gobject-introspection
# cogl-1.0 needs opengl to build
REQUIRED_DISTRO_FEATURES ?= "opengl"

DEPENDS = "glib-2.0 gdk-pixbuf"
PACKAGES =+ "${PN}-examples \
             libcogl libcogl-gles2 libcogl-pango libcogl-path \
            "
AUTOTOOLS_AUXDIR = "${S}/build"

# Extra DEPENDS for PACKAGECONFIG
EDEPENDS_GL = "virtual/libgl libdrm"
EDEPENDS_GLES2 = "virtual/libgles2"
EDEPENDS_KMS = "libdrm virtual/egl"
EDEPENDS_EGL = "virtual/egl"
EDEPENDS_X11 = "virtual/libx11 libxcomposite libxfixes libxi libxrandr"
EDEPENDS_WAYLAND = "virtual/mesa wayland"

# Extra RDEPENDS for PACKAGECONFIG
# This has to be explictly listed, because cogl dlopens the backends
ERDEPENDS_GL    = "libgl"
ERDEPENDS_GLES2 = "libgles2"

# GLESv1 is rarely tested, so disable it
EXTRA_OECONF += "--enable-examples-install	\
	       	 --enable-debug		 	\
	       	 --disable-gles1		\
	         --disable-cairo                \
	    	"

# OpenGL/GLX
PACKAGECONFIG[glx] = "--enable-gl --enable-glx,--disable-gl --disable-glx,${EDEPENDS_GL} ${EDEPENDS_X11},${ERDEPENDS_GL}"

# GLESv2
PACKAGECONFIG[gles2] = "--enable-gles2,--disable-gles2,${EDEPENDS_GLES2}, ${ERDEPENDS_GLES2}"

# EGL backends
PACKAGECONFIG[egl-kms] = "--enable-kms-egl-platform,--disable-kms-egl-platform,${EDEPENDS_KMS}"
PACKAGECONFIG[egl-null] = "--enable-null-egl-platform,--disable-null-egl-platform"
PACKAGECONFIG[egl-x11] = "--enable-xlib-egl-platform,--disable-xlib-egl-platform,${EDEPENDS_X11} ${EDEPENDS_EGL}"
PACKAGECONFIG[egl-wayland] = "--enable-wayland-egl-platform,--disable-wayland-egl-platform,${EDEPENDS_WAYLAND}"

# Wayland (server-side)
PACKAGECONFIG[wayland-server] = "--enable-wayland-egl-server,--disable-wayland-egl-server,${EDEPENDS_WAYLAND}"

# Support rendering text directly with Pango
PACKAGECONFIG[cogl-pango] = "--enable-cogl-pango,--disable-cogl-pango,pango"

# Respect the DISTRO_FEATURES to pull in GLX or Wayland as appropriate by
# default.
PACKAGECONFIG ??= "cogl-pango gles2 \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'glx egl-x11', '', d)} \
                   "

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/cogl/.libs"
}

FILES_${PN} = ""
FILES_${PN}-examples = "${bindir}/* ${datadir}/cogl/examples-data/*"
FILES_libcogl = "${libdir}/libcogl${SOLIBS} ${libdir}/girepository-1.0/Cogl-*.typelib"
FILES_libcogl-gles2 = "${libdir}/libcogl-gles2${SOLIBS}"
FILES_libcogl-pango = "${libdir}/libcogl-pango${SOLIBS} ${libdir}/girepository-1.0/CoglPango*.typelib"

FILES_libcogl-path = "${libdir}/libcogl-path${SOLIBS}"

# For backwards compatibility after Debian-renaming
RPROVIDES_libcogl = "cogl-1.0"
RCONFLICTS_libcogl = "cogl-1.0"
RREPLACES_libcogl = "cogl-1.0"

COMPATIBLE_HOST_armv4 = 'null'
