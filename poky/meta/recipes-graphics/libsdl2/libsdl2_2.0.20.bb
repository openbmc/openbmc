SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "Zlib & BSD-2-Clause"
LIC_FILES_CHKSUM = "\
    file://LICENSE.txt;md5=68a088513da90254b2fbe664f42af315 \
    file://src/hidapi/LICENSE.txt;md5=7c3949a631240cb6c31c50f3eb696077 \
    file://src/hidapi/LICENSE-bsd.txt;md5=b5fa085ce0926bb50d0621620a82361f \
    file://src/video/yuv2rgb/LICENSE;md5=79f8f3418d91531e05f0fc94ca67e071 \
"

# arm-neon adds MIT license
LICENSE:append = " ${@bb.utils.contains('PACKAGECONFIG', 'arm-neon', '& MIT', '', d)}"
LIC_FILES_CHKSUM:append = " ${@bb.utils.contains('PACKAGECONFIG', 'arm-neon', 'file://src/video/arm/pixman-arm-neon-asm.h;md5=9a9cc1e51abbf1da58f4d9528ec9d49b;beginline=1;endline=24', '', d)}"

PROVIDES = "virtual/libsdl2"

SRC_URI = "http://www.libsdl.org/release/SDL2-${PV}.tar.gz \
           file://optional-libunwind-generic.patch \
           file://0001-sdlchecks.cmake-pass-cflags-to-the-appropriate-cmake.patch \
           "
SRC_URI:append:class-native = " file://0001-Disable-libunwind-in-native-OE-builds-by-not-looking.patch"

S = "${WORKDIR}/SDL2-${PV}"

SRC_URI[sha256sum] = "c56aba1d7b5b0e7e999e4a7698c70b63a3394ff9704b5f6e1c57e0c16f04dd06"

inherit cmake lib_package binconfig-disabled pkgconfig

BINCONFIG = "${bindir}/sdl2-config"

CVE_PRODUCT = "simple_directmedia_layer sdl"

EXTRA_OECMAKE = "-DSDL_OSS=OFF -DSDL_ESD=OFF -DSDL_ARTS=OFF \
                 -DSDL_DISKAUDIO=OFF -DSDL_NAS=OFF -DSDL_ESD_SHARED=OFF \
                 -DSDL_DUMMYVIDEO=OFF \
                 -DSDL_RPI=OFF \
                 -DSDL_PTHREADS=ON \
                 -DSDL_RPATH=OFF \
                 -DSDL_SNDIO=OFF \
                 -DSDL_X11_XVM=OFF \
                 -DSDL_X11_XCURSOR=OFF \
                 -DSDL_X11_XINERAMA=OFF \
                 -DSDL_X11_XDBE=OFF \
                 -DSDL_X11_XFIXES=OFF \
                 -DSDL_X11_XINPUT=OFF \
                 -DSDL_X11_XRANDR=OFF \
                 -DSDL_X11_XSCRNSAVER=OFF \
                 -DSDL_X11_XSHAPE=OFF \
"

# opengl packageconfig factored out to make it easy for distros
# and BSP layers to pick either (desktop) opengl, gles2, or no GL
PACKAGECONFIG_GL ?= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

PACKAGECONFIG:class-native = "x11 ${PACKAGECONFIG_GL}"
PACKAGECONFIG:class-nativesdk = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} ${PACKAGECONFIG_GL}"
PACKAGECONFIG ??= " \
    ${PACKAGECONFIG_GL} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa directfb pulseaudio x11', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland gles2', '', d)} \
    ${@bb.utils.contains("TUNE_FEATURES", "neon","arm-neon","",d)} \
"
PACKAGECONFIG[alsa]       = "-DSDL_ALSA=ON,-DSDL_ALSA=OFF,alsa-lib,"
PACKAGECONFIG[arm-neon]   = "-DSDL_ARMNEON=ON,-DSDL_ARMNEON=OFF"
PACKAGECONFIG[directfb]   = "-DSDL_DIRECTFB=ON,-DSDL_DIRECTFB=OFF,directfb,directfb"
PACKAGECONFIG[gles2]      = "-DSDL_OPENGLES=ON,-DSDL_OPENGLES=OFF,virtual/libgles2"
PACKAGECONFIG[jack]       = "-DSDL_JACK=ON,-DSDL_JACK=OFF,jack"
PACKAGECONFIG[kmsdrm]     = "-DSDL_KMSDRM=ON,-DSDL_KMSDRM=OFF,libdrm virtual/libgbm"
PACKAGECONFIG[opengl]     = "-DSDL_OPENGL=ON,-DSDL_OPENGL=OFF,virtual/egl"
PACKAGECONFIG[pulseaudio] = "-DSDL_PULSEAUDIO=ON,-DSDL_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[wayland]    = "-DSDL_WAYLAND=ON,-DSDL_WAYLAND=OFF,wayland-native wayland wayland-protocols libxkbcommon"
PACKAGECONFIG[x11]        = "-DSDL_X11=ON,-DSDL_X11=OFF,virtual/libx11 libxext libxrandr libxrender"

CFLAGS:append:class-native = " -DNO_SHARED_MEMORY"

BBCLASSEXTEND = "native nativesdk"
