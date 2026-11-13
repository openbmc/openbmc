HOMEPAGE = "https://github.com/mchehab/zbar"
SUMMARY = "A bar code library"
DESRIPTION = "ZBar is an open source software suite for reading bar codes \
from various sources, such as video streams, image files and raw \
intensity sensors. It supports EAN-13/UPC-A, UPC-E, EAN-8, Code 128, \
Code 93, Code 39, Codabar, Interleaved 2 of 5, QR Code and SQ Code"
SECTION = "graphics"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=5e9ee833a2118adc7d8b5ea38e5b1cef"

SRC_URI = "git://github.com/mchehab/zbar.git;branch=master;protocol=https;tag=${PV} \
           file://0001-test-meson.build-better-detect-if-libc-has-argp.patch \
           "
SRCREV = "dc19fcc7da58e8cf84ba1ec417e24b04609feb23"

DEPENDS += "xmlto-native"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    video \
"

inherit meson pkgconfig gettext \
    ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3native', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gtk', 'gobject-introspection', '', d)}

PACKAGECONFIG[x11] = "-Dx11=enabled -Dxv=enabled -Dxshm=enabled,-Dx11=disabled -Dxv=disabled -Dxshm=disabled,libx11 libxext libxv"
PACKAGECONFIG[video] = "-Dvideo=true,-Dvideo=false,v4l-utils libv4l"
PACKAGECONFIG[jpeg] = "-Djpeg=enabled,-Djpeg=disabled,jpeg"
PACKAGECONFIG[python] = "-Dpython=python3,-Dpython=no,python3"
PACKAGECONFIG[gtk] = "-Dgtk=gtk3,-Dgtk=no,gtk+3"
PACKAGECONFIG[qt] = "-Dqt=true,-Dqt=false,qtbase qtbase-native qtx11extras qtsvg,qtbase"
PACKAGECONFIG[imagemagick] = "-Dimagemagick=enabled -Dgraphicsmagick=disabled,-Dimagemagick=disabled -Dgraphicsmagick=disabled,imagemagick"

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
