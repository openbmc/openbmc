SUMMARY = "Delivery framework for general Input Method configuration"
DESCRIPTION = "IMSettings is a framework that delivers Input Method \
settings and applies the changes so they take effect \
immediately without any need to restart applications \
or the desktop. \
This package contains the core DBus services and some utilities."
HOMEPAGE = "http://code.google.com/p/imsettings/"
SECTION = "Applications/System"

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools gtk-doc gobject-introspection gettext features_check

DEPENDS = "autoconf-archive-native gtk+3 libnotify"

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://bitbucket.org/tagoh/imsettings/downloads/${BPN}-${PV}.tar.bz2 \
           file://imsettings-gcc10.patch \
          "
SRC_URI[sha256sum] = "45986b9ca1b87b760a5dbaecd9a2b77d080adc47868a0512826077175d5b3ee3"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

PACKAGECONFIG ??= "xfce"
PACKAGECONFIG[xfce] = ",,xfconf"
PACKAGECONFIG[xim] = ",,libgxim"

RDEPENDS:${PN} += "bash"

FILES:${PN} += "${datadir}/dbus-1/*"
