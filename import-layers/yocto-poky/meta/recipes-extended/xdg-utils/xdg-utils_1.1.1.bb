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

SRC_URI[md5sum] = "2d0aec6037769a5f138ff404b1bb4b15"
SRC_URI[sha256sum] = "b0dd63a2576e0bb16f1aa78d6ddf7d6784784a098d4df17161cd6a17c7bc4125"

UPSTREAM_CHECK_REGEX = "xdg-utils-(?P<pver>((\d+[\.\-_]*)+)((rc|alpha|beta)\d+)?)\.(tar\.gz|tgz)"

# Needs brokensep as this doesn't use automake
inherit autotools-brokensep distro_features_check

# The xprop requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "xmlto-native libxslt-native"
RDEPENDS_${PN} += "xprop"
