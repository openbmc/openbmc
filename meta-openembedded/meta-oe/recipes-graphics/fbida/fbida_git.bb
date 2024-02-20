SUMMARY = "Framebuffer image and doc viewer tools"
DESCRIPTION = "The fbida project contains a few applications for viewing and editing images, \
               with the main focus being photos."
HOMEPAGE = "https://www.kraxel.org/blog/linux/fbida/"
SECTION = "utils"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e8feb78a32950a909621bbb51f634b39"

DEPENDS = "virtual/libiconv jpeg fontconfig freetype libexif libdrm pixman udev libinput cairo"

PV = "2.14+git"
SRC_URI = "git://github.com/kraxel/fbida;protocol=https;branch=master \
           file://fix-preprocessor.patch \
           file://support-jpeg-turbo.patch \
           file://fbida-gcc10.patch \
           file://0001-meson.build-install-fbgs-shell-script.patch \
           file://0002-meson.build-add-features-options-for-png-gif-tiff-we.patch \
           file://0003-meson.build-do-not-require-xkbcommon.patch \
           file://0001-meson.build-make-fbpdf-build-optional.patch \
           file://0001-fbida-Include-missing-sys-types.h.patch \
"
SRCREV = "eb769e3d7f4a073d4c37ed524ebd5017c6a578f5"
S = "${WORKDIR}/git"

inherit meson pkgconfig features_check

# Depends on libepoxy
REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "gif png pdf"
PACKAGECONFIG[gif] = "-Dgif=enabled,-Dgif=disabled,giflib"
PACKAGECONFIG[png] = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[tiff] = "-Dtiff=enabled,-Dtiff=disabled,tiff"
PACKAGECONFIG[motif] = "-Dmotif=enabled,-Dmotif=disabled,libx11 libxext libxpm libxt openmotif"
PACKAGECONFIG[webp] = "-Dwebp=enabled,-Dwebp=disabled,libwebp"
PACKAGECONFIG[pdf] = "-Dpdf=enabled,-Dpdf=disabled,poppler libepoxy"

CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '-DEGL_NO_X11=1', d)}"

RDEPENDS:${PN} = "ttf-dejavu-sans-mono"
