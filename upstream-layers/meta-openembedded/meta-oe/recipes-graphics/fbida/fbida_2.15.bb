SUMMARY = "Framebuffer image and doc viewer tools"
DESCRIPTION = "The fbida project contains a few applications for viewing and editing images, \
               with the main focus being photos."
HOMEPAGE = "https://www.kraxel.org/blog/linux/fbida/"
SECTION = "utils"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e8feb78a32950a909621bbb51f634b39"

DEPENDS = "virtual/libiconv jpeg fontconfig freetype libexif libdrm pixman udev libinput cairo libxkbcommon"

GITTAG = "${PN}-${PV}-1"
SRC_URI = "git://github.com/kraxel/fbida;protocol=https;branch=master;tag=${GITTAG} \
           file://fix-preprocessor.patch \
           file://support-jpeg-turbo.patch \
"
SRCREV = "a0d75fbab3ea01bf5b36f813f0ec0d1bfa2db745"

inherit meson pkgconfig features_check

# Depends on libepoxy
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'pdf', 'opengl', '', d)}"

PACKAGECONFIG ??= "gif png pdf"
PACKAGECONFIG[gif] = "-Dgif=enabled,-Dgif=disabled,giflib"
PACKAGECONFIG[png] = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[tiff] = "-Dtiff=enabled,-Dtiff=disabled,tiff"
PACKAGECONFIG[motif] = "-Dmotif=enabled,-Dmotif=disabled,libx11 libxext libxpm libxt openmotif"
PACKAGECONFIG[webp] = "-Dwebp=enabled,-Dwebp=disabled,libwebp"
PACKAGECONFIG[pdf] = "-Dpdf=enabled,-Dpdf=disabled,poppler libepoxy"

CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '-DEGL_NO_X11=1', d)}"

RDEPENDS:${PN} = "ttf-dejavu-sans-mono"
