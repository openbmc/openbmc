SUMMARY = "Delivery framework for general Input Method configuration"
DESCRIPTION = "IMSettings is a framework that delivers Input Method \
settings and applies the changes so they take effect \
immediately without any need to restart applications \
or the desktop. \
This package contains the core DBus services and some utilities."
HOMEPAGE = "http://code.google.com/p/imsettings/"
SECTION = "Applications/System"

inherit autotools gtk-doc gobject-introspection

SRC_URI = "https://bitbucket.org/tagoh/imsettings/downloads/${BPN}-${PV}.tar.bz2 \
           file://gtk-is-required-by-notify.patch \
           file://multi-line-ACLOCAL_AMFLAGS-isnot-supported-by-autoreconf.patch \
           file://0001-Rename-use-of-stdout-and-stderr.patch \
           "

SRC_URI[md5sum] = "8153b0583a9f47d8a62af1f92fb9d3bf"
SRC_URI[sha256sum] = "41addf0458f760212b6b6100835066e16deb3a8a50eb005a0fc17fbac0a2ae66"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

DEPENDS = "gtk+ gconf libnotify dbus-glib libgxim xfconf intltool-native"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/dbus-1/* ${datadir}/gir-1.0/* ${libdir}/girepository-1.0/*"
