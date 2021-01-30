SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=2d4af6adb4d89aad0cdedbcc18c9a32f"

# arm-neon adds MIT license
LICENSE_append = " ${@bb.utils.contains('PACKAGECONFIG', 'arm-neon', '& MIT', '', d)}"
LIC_FILES_CHKSUM_append = " ${@bb.utils.contains('PACKAGECONFIG', 'arm-neon', 'file://src/video/arm/pixman-arm-neon-asm.h;md5=9a9cc1e51abbf1da58f4d9528ec9d49b;beginline=1;endline=24', '', d)}"

PROVIDES = "virtual/libsdl2"

SRC_URI = "http://www.libsdl.org/release/SDL2-${PV}.tar.gz \
           file://more-gen-depends.patch \
           file://directfb-spurious-curly-brace-missing-e.patch \
           file://directfb-renderfillrect-fix.patch \
"

S = "${WORKDIR}/SDL2-${PV}"

SRC_URI[md5sum] = "783b6f2df8ff02b19bb5ce492b99c8ff"
SRC_URI[sha256sum] = "349268f695c02efbc9b9148a70b85e58cefbbf704abd3e91be654db7f1e2c863"

inherit autotools lib_package binconfig-disabled pkgconfig

BINCONFIG = "${bindir}/sdl2-config"

CVE_PRODUCT = "simple_directmedia_layer sdl"

EXTRA_OECONF = "--disable-oss --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
                --disable-video-dummy \
                --enable-pthreads \
                --enable-sdl-dlopen \
                --disable-rpath \
                --disable-sndio \
                --disable-fcitx --disable-ibus \
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
    ${@bb.utils.contains("TUNE_FEATURES", "neon","arm-neon","",d)} \
"
PACKAGECONFIG[alsa]       = "--enable-alsa --disable-alsatest,--disable-alsa,alsa-lib,"
PACKAGECONFIG[arm-neon]   = "--enable-arm-neon,--disable-arm-neon"
PACKAGECONFIG[directfb]   = "--enable-video-directfb,--disable-video-directfb,directfb,directfb"
PACKAGECONFIG[gles2]      = "--enable-video-opengles,--disable-video-opengles,virtual/libgles2"
PACKAGECONFIG[jack]       = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[kmsdrm]     = "--enable-video-kmsdrm,--disable-video-kmsdrm,libdrm virtual/libgbm"
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
