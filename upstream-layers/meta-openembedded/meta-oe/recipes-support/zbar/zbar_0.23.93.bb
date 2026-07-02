HOMEPAGE = "https://github.com/mchehab/zbar"
SUMMARY = "A bar code library"
DESRIPTION = "ZBar is an open source software suite for reading bar codes \
from various sources, such as video streams, image files and raw \
intensity sensors. It supports EAN-13/UPC-A, UPC-E, EAN-8, Code 128, \
Code 93, Code 39, Codabar, Interleaved 2 of 5, QR Code and SQ Code"
SECTION = "graphics"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=5e9ee833a2118adc7d8b5ea38e5b1cef"

SRC_URI = "git://github.com/mchehab/zbar.git;branch=master;protocol=https;tag=${PV}"
SRCREV = "bb05ec54eec57f8397cb13fb9161372a281a1219"

DEPENDS += "xmlto-native"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    video \
"

inherit autotools pkgconfig gettext \
    ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3native', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gtk', 'gobject-introspection', '', d)}

PACKAGECONFIG[x11] = ",, libxext libxv"
PACKAGECONFIG[video] = "--enable-video, --disable-video, v4l-utils libv4l"
PACKAGECONFIG[jpeg] = "--with-jpeg, --without-jpeg, jpeg"
PACKAGECONFIG[python] = "--with-python=auto, --without-python, python3"
PACKAGECONFIG[gtk] = "--with-gtk=auto, --without-gtk, gtk+3"
PACKAGECONFIG[qt] = "--with-qt, --without-qt, qtbase qtbase-native qtx11extras qtsvg, qtbase"
PACKAGECONFIG[imagemagick] = "--with-imagemagick, --without-imagemagick, imagemagick"

EXTRA_OECONF += "--enable-pthread"

FILES:${PN} += "${bindir} \
    ${@bb.utils.contains('DEPENDS', 'python3-native', '${libdir}', '', d)} \
"

CPPFLAGS:append = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'qt', '\
    -I${STAGING_INCDIR}/QtX11Extras \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    ', '', d)} \
"

TARGET_CXXFLAGS:append = " -fPIC"

do_configure:prepend() {
    install -m 755 ${STAGING_DATADIR_NATIVE}/gettext/ABOUT-NLS ${S}/
}
