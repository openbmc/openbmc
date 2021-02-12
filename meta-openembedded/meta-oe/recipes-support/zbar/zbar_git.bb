HOMEPAGE = "https://github.com/mchehab/zbar"
SUMMARY = "A bar code library"
DESRIPTION = "ZBar is an open source software suite for reading bar codes \
from various sources, such as video streams, image files and raw \
intensity sensors. It supports EAN-13/UPC-A, UPC-E, EAN-8, Code 128, \
Code 93, Code 39, Codabar, Interleaved 2 of 5, QR Code and SQ Code"
SECTION = "graphics"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=5e9ee833a2118adc7d8b5ea38e5b1cef"

SRC_URI = "git://github.com/mchehab/zbar.git;branch=master \
    file://0001-qt-Create-subdir-in-Makefile.patch \
    file://0002-zbarcam-Create-subdir-in-Makefile.patch \
"
SRCREV = "89e7900d85dd54ef351a7ed582aec6a5a5d7fa37"

S = "${WORKDIR}/git"
PV = "0.23.1+git${SRCPV}"

DEPENDS += "xmlto-native"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
"

PACKAGECONFIG ??= "video python3"

inherit autotools pkgconfig gettext \
    ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gtk3', 'gobject-introspection',	'', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'qt5', 'qmake5_paths', '', d)}

PACKAGECONFIG[x11] = "--with-x, --without-x, libxv"
PACKAGECONFIG[video] = "--enable-video, --disable-video, v4l-utils libv4l"
PACKAGECONFIG[jpeg] = "--with-jpeg, --without-jpeg, jpeg"
PACKAGECONFIG[python3] = "--with-python=auto, --without-python, python3"
PACKAGECONFIG[gtk3] = "--with-gtk=gtk3, --without-gtk, gtk+3"
PACKAGECONFIG[qt5] = "--with-qt5, --without-qt5, qtbase qtbase-native qtx11extras qtsvg, qtbase"
PACKAGECONFIG[imagemagick] = "--with-imagemagick, --without-imagemagick, imagemagick"

FILES_${PN} += "${bindir} \
    ${@bb.utils.contains('DEPENDS', 'python3-native', '${libdir}', '', d)} \
"

CPPFLAGS_append = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'qt5', '\
    -I${STAGING_INCDIR}/QtX11Extras \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    ', '', d)} \
"

TARGET_CXXFLAGS_append = " -fPIC"

do_configure_prepend() {
    install -m 755 ${STAGING_DATADIR_NATIVE}/gettext/ABOUT-NLS ${S}/
}
