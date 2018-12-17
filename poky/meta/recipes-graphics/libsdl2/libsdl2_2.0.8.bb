SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=02ee26814dd044bd7838ae24e05b880f"

PROVIDES = "virtual/libsdl2"

SRC_URI = "http://www.libsdl.org/release/SDL2-${PV}.tar.gz \
           file://more-gen-depends.patch \
           file://0001-GLES2-Get-sin-cos-out-of-vertex-shader.patch \
"

S = "${WORKDIR}/SDL2-${PV}"

SRC_URI[md5sum] = "3800d705cef742c6a634f202c37f263f"
SRC_URI[sha256sum] = "edc77c57308661d576e843344d8638e025a7818bff73f8fbfab09c3c5fd092ec"

inherit autotools lib_package binconfig pkgconfig

EXTRA_OECONF = "--disable-oss --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
                --disable-video-dummy \
                --enable-pthreads \
                --enable-sdl-dlopen \
                --disable-rpath \
                --disable-sndio \
                "

# opengl packageconfig factored out to make it easy for distros
# and BSP layers to pick either (desktop) opengl, gles2, or no GL
PACKAGECONFIG_GL ?= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

PACKAGECONFIG_class-native = "x11"
PACKAGECONFIG_class-nativesdk = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG ??= " \
    ${PACKAGECONFIG_GL} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa directfb pulseaudio x11', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland gles2', '', d)} \
"
PACKAGECONFIG[alsa]       = "--enable-alsa --disable-alsatest,--disable-alsa,alsa-lib,"
PACKAGECONFIG[directfb]   = "--enable-video-directfb,--disable-video-directfb,directfb"
PACKAGECONFIG[gles2]      = "--enable-video-opengles,--disable-video-opengles,virtual/libgles2"
PACKAGECONFIG[opengl]     = "--enable-video-opengl,--disable-video-opengl,virtual/libgl"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio,--disable-pulseaudio,pulseaudio"
PACKAGECONFIG[tslib]      = "--enable-input-tslib,--disable-input-tslib,tslib"
PACKAGECONFIG[wayland]    = "--enable-video-wayland,--disable-video-wayland,wayland-native wayland wayland-protocols libxkbcommon"
PACKAGECONFIG[x11]        = "--enable-video-x11,--disable-video-x11,virtual/libx11 libxext libxrandr libxrender"

EXTRA_AUTORECONF += "--include=acinclude --exclude=autoheader"

do_configure_prepend() {
        # Remove old libtool macros.
        MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
        for i in ${MACROS}; do
               rm -f ${S}/acinclude/$i
        done
        export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}

FILES_${PN}-dev += "${libdir}/cmake"

BBCLASSEXTEND = "native nativesdk"
