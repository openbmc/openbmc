SUMMARY = "Delivery framework for general Input Method configuration"
DESCRIPTION = "IMSettings is a framework that delivers Input Method \
settings and applies the changes so they take effect \
immediately without any need to restart applications \
or the desktop. \
This package contains the core DBus services and some utilities."
HOMEPAGE = "http://code.google.com/p/imsettings/"
SECTION = "Applications/System"

inherit autotools gtk-doc gobject-introspection distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://bitbucket.org/tagoh/imsettings/downloads/${BPN}-${PV}.tar.bz2 \
           "

SRC_URI[md5sum] = "c6c65a2b2654fe9dfe9ab2e8b80c079a"
SRC_URI[sha256sum] = "196d3a74cef254ff812c32682d1818d740e36a68b976fef9e99748d23a71a71a"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

DEPENDS = "gtk+ gconf libnotify dbus-glib libgxim xfconf intltool-native"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/dbus-1/* ${datadir}/gir-1.0/* ${libdir}/girepository-1.0/*"
