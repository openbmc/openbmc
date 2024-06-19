SUMMARY = "Graphics abstraction library for the Linux Framebuffer Device"
DESCRIPTION = "DirectFB is a thin library that provides developers \
with hardware graphics acceleration, input device handling and \
abstraction, an integrated windowing system with support for \
translucent windows and multiple display layers on top of the \
Linux framebuffer device."
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"

HOMEPAGE = "http://directfb.org"
DEPENDS = "jpeg libpng freetype zlib sysfsutils"

SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/DirectFB-${PV}.tar.gz \
           file://configurefix.patch \
           file://fusion.patch \
           file://bashism.patch \
           file://0001-gfx-direct-Aboid-usng-VLAs-and-printf-formats.patch \
           file://compar_fn_t.patch \
           file://union-sigval.patch \
           file://use-PTHREAD_MUTEX_RECURSIVE.patch \
           file://fix-client-gfx_state-initialisation.patch \
           file://fix-tslib-version-check.patch \
           file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch \
           file://0001-os-linux-Fix-build-when-__NR_futex-is-not-available.patch \
           file://0001-include-libgen.h-for-basename.patch \
           file://0001-inputdrivers-Correct-the-signature-of-bind-call-on-m.patch \
           "

S = "${WORKDIR}/DirectFB-${PV}"

LDFLAGS:append = " -lm"

CXXFLAGS:append:toolchain-clang = " -Wno-error=dtor-typedef"

# Workaround for linking issues seen with armv7a + gold
LDFLAGS:append:arm = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

BINCONFIG = "${bindir}/directfb-config"

inherit autotools binconfig-disabled pkgconfig

PACKAGECONFIG ??= "linuxinput"
PACKAGECONFIG[jpeg2000] = "--enable-jpeg2000,--disable-jpeg2000,jasper"
PACKAGECONFIG[drmkms] = "--enable-drmkms,--disable-drmkms,libdrm"
PACKAGECONFIG[tiff] = "--enable-tiff,--disable-tiff,tiff"
PACKAGECONFIG[webp] = "--enable-webp,--disable-webp,libwebp"
PACKAGECONFIG[tslib] = "--with-inputdrivers=tslib,,tslib"
PACKAGECONFIG[linuxinput] = "--with-inputdrivers=linuxinput,,"
PACKAGECONFIG[noinput] = "--with-inputdrivers=none,,"
PACKAGECONFIG[mesa] = "--enable-mesa,--disable-mesa,virtual/mesa,"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,xserver-xorg,"

EXTRA_OECONF = "\
  --with-gfxdrivers=none \
  --enable-freetype=yes \
  --enable-zlib \
  --disable-imlib2 \
  --disable-sdl \
  --disable-vnc \
  --disable-x11vdpau \
  --disable-xine-vdpau \
"

#Once -fno-omit-frame-pointer option of gcc is added into TARGET_CLFAGS as default
#this will cause directfb build failure on x86 arch, so filter out it.
TARGET_CFLAGS:remove:x86 = "-fno-omit-frame-pointer"

#PACKAGES_DYNAMIC += "^directfb-inputdrivers-.*"
#
#python populate_packages:prepend () {
#    inputdrivers_libdir = d.expand('${libdir}/directfb-${RV}/inputdrivers')
#    do_split_packages(d, inputdrivers_libdir, '*.so$', 'directfb-inputdrivers-%s', 'Directfb plugin for %s')
#}

# NOTE: monolithic packaging for now, should improve that eventually

FILES:${PN}-dev += "\
  ${bindir}/directfb-config \
  ${libdir}/directfb-${RV}/systems/*.la \
  ${libdir}/directfb-${RV}/inputdrivers/*.la \
  ${libdir}/directfb-${RV}/interfaces/*/*.la \
  ${libdir}/directfb-${RV}/wm/*.la \
"

FILES:${PN} += "\
  ${libdir}/directfb-${RV}/systems/*.so \
  ${libdir}/directfb-${RV}/inputdrivers/*.so \
  ${libdir}/directfb-${RV}/interfaces/*/*.so \
  ${libdir}/directfb-${RV}/wm/*.so \
  ${datadir}/directfb-${PV} \
"
