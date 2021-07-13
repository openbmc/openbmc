SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=27818cd7fd83877a8e3ef82b82798ef4"

PROVIDES = "virtual/libsdl"

PR = "r3"

SRC_URI = "http://www.libsdl.org/release/SDL-${PV}.tar.gz \
           file://libsdl-1.2.15-xdata32.patch \
           file://pkgconfig.patch \
           file://0001-build-Pass-tag-CC-explictly-when-using-libtool.patch \
           file://CVE-2019-7577.patch \
           file://CVE-2019-7574.patch \
           file://CVE-2019-7572.patch \
           file://CVE-2019-7578.patch \
           file://CVE-2019-7575.patch \
           file://CVE-2019-7635.patch \
           file://CVE-2019-7637.patch \
           file://CVE-2019-7638.patch \
           file://CVE-2019-7576.patch \
           file://CVE-2019-13616.patch \
          "

UPSTREAM_CHECK_REGEX = "SDL-(?P<pver>\d+(\.\d+)+)\.tar"

S = "${WORKDIR}/SDL-${PV}"

SRC_URI[md5sum] = "9d96df8417572a2afb781a7c4c811a85"
SRC_URI[sha256sum] = "d6d316a793e5e348155f0dd93b979798933fb98aa1edebcc108829d6474aad00"

BINCONFIG = "${bindir}/sdl-config"

inherit autotools lib_package binconfig-disabled pkgconfig

CVE_PRODUCT = "simple_directmedia_layer sdl"

EXTRA_OECONF = "--disable-static --enable-cdrom --enable-threads --enable-timers \
                --enable-file --disable-oss --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas \
                --disable-mintaudio --disable-nasm --disable-video-dga \
                --disable-video-fbcon --disable-video-ps2gs --disable-video-ps3 \
                --disable-xbios --disable-gem --disable-video-dummy \
                --enable-input-events --enable-pthreads \
                --disable-video-svga \
                --disable-video-picogui --disable-video-qtopia --enable-sdl-dlopen \
                --disable-rpath"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa directfb pulseaudio x11', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'opengl', '', d)}"
PACKAGECONFIG_class-native = "x11"
PACKAGECONFIG_class-nativesdk = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[alsa] = "--enable-alsa --disable-alsatest,--disable-alsa,alsa-lib"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio,--disable-pulseaudio,pulseaudio"
PACKAGECONFIG[tslib] = "--enable-input-tslib, --disable-input-tslib, tslib"
PACKAGECONFIG[directfb] = "--enable-video-directfb, --disable-video-directfb, directfb"
PACKAGECONFIG[opengl] = "--enable-video-opengl, --disable-video-opengl, virtual/libgl libglu"
PACKAGECONFIG[x11] = "--enable-video-x11 --disable-x11-shared, --disable-video-x11, virtual/libx11 libxext libxrandr libxrender"

# The following two options should only enabled with mingw support
PACKAGECONFIG[stdio-redirect] = "--enable-stdio-redirect,--disable-stdio-redirect"
PACKAGECONFIG[directx] = "--enable-directx,--disable-directx"

EXTRA_AUTORECONF += "--include=acinclude --exclude=autoheader"

do_configure_prepend() {
        # Remove old libtool macros.
        MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
        for i in ${MACROS}; do
               rm -f ${S}/acinclude/$i
        done
        export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}

BBCLASSEXTEND = "native nativesdk"
