SUMMARY = "Basic desktop integration functions"
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/xdg-utils/"
DESCRIPTION = "The xdg-utils package is a set of simple scripts that provide basic \
desktop integration functions for any Free Desktop, such as Linux. \
They are intended to provide a set of defacto standards. \
The following scripts are provided at this time: \
xdg-desktop-icon \
xdg-desktop-menu \
xdg-email \
xdg-icon-resource \
xdg-mime \
xdg-open \
xdg-screensaver \
xdg-terminal \
"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a5367a90934098d6b05af3b746405014"

SRC_URI = "http://portland.freedesktop.org/download/${BPN}-${PV}.tar.gz \
           file://0001-Reinstate-xdg-terminal.patch \
           file://0001-Don-t-build-the-in-script-manual.patch \
          "

SRC_URI[md5sum] = "361e75eb76c94d19f6f4f330d8ee626b"
SRC_URI[sha256sum] = "951952e2c6bb21214e0bb54e0dffa057d30f5563300225c24c16fba846258bcc"

UPSTREAM_CHECK_REGEX = "xdg-utils-(?P<pver>((\d+[\.\-_]*)+)((rc|alpha|beta)\d+)?)\.(tar\.gz|tgz)"

# Needs brokensep as this doesn't use automake
inherit autotools-brokensep distro_features_check

# The xprop requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "xmlto-native libxslt-native"
RDEPENDS_${PN} += "xprop"
