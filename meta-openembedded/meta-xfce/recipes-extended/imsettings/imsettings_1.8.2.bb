SUMMARY = "Delivery framework for general Input Method configuration"
DESCRIPTION = "IMSettings is a framework that delivers Input Method \
settings and applies the changes so they take effect \
immediately without any need to restart applications \
or the desktop. \
This package contains the core DBus services and some utilities."
HOMEPAGE = "http://code.google.com/p/imsettings/"
SECTION = "Applications/System"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools gtk-doc gobject-introspection gettext features_check

DEPENDS = "gtk+3 libnotify"

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://bitbucket.org/tagoh/imsettings/downloads/${BPN}-${PV}.tar.bz2 \
           file://imsettings-gcc10.patch \
          "
SRC_URI[sha256sum] = "412abf3165dbee3cbe03db0c296bab103569a49029429d038569c586ebe9efa9"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

PACKAGECONFIG ??= "xfce"
PACKAGECONFIG[xfce] = ",,xfconf"
PACKAGECONFIG[xim] = ",,libgxim"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/dbus-1/*"
