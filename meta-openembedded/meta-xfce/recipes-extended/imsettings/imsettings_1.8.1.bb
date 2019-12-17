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

SRC_URI = "https://bitbucket.org/tagoh/imsettings/downloads/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "c04341a008d8c60e1532f033f4020f44"
SRC_URI[sha256sum] = "ebf578299a10c6a1fea9047be6577df6d2b6470d3cc9d40f8a6589a3c13c4c8b"

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

PACKAGECONFIG ??= "xfce"
PACKAGECONFIG[xfce] = ",,xfconf"
PACKAGECONFIG[xim] = ",,libgxim"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${datadir}/dbus-1/*"
