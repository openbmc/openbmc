SUMMARY = "Delivery framework for general Input Method configuration"
DESCRIPTION = "IMSettings is a framework that delivers Input Method \
settings and applies the changes so they take effect \
immediately without any need to restart applications \
or the desktop. \
This package contains the core DBus services and some utilities."
HOMEPAGE = "http://code.google.com/p/imsettings/"
SECTION = "Applications/System"

inherit autotools gtk-doc

SRC_URI = "https://bitbucket.org/tagoh/imsettings/downloads/${BPN}-${PV}.tar.bz2 \
           file://gtk-is-required-by-notify.patch \
           file://multi-line-ACLOCAL_AMFLAGS-isnot-supported-by-autoreconf.patch"

SRC_URI[md5sum] = "ab439e21a7d86fa99fbc04586c755349"
SRC_URI[sha256sum] = "12c35352386057ba68d69a0b7d9a1d0d01ebbd893aafe0a094c3158c8079ac9a"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

DEPENDS = "gtk+ gconf libnotify dbus-glib libgxim xfconf intltool-native"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/dbus-1/* ${datadir}/gir-1.0/* ${libdir}/girepository-1.0/*"
