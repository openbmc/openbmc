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

SRC_URI[md5sum] = "c31429f1d60e36d7f811f871c75b6c41"
SRC_URI[sha256sum] = "2620ffbf9a6dc842dbf994b4773d4fe355eb77076ccf33f726ba63f16c0d08ba"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

DEPENDS = "gtk+ gconf libnotify dbus-glib libgxim xfconf"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/dbus-1/*"
