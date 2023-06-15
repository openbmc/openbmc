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

SRC_URI = "https://portland.freedesktop.org/download/${BPN}-${PV}.tar.gz \
           file://0001-Reinstate-xdg-terminal.patch \
           file://0001-Don-t-build-the-in-script-manual.patch \
           file://1f199813e0eb0246f63b54e9e154970e609575af.patch \
           file://CVE-2022-4055.patch \
          "

SRC_URI[md5sum] = "902042508b626027a3709d105f0b63ff"
SRC_URI[sha256sum] = "d798b08af8a8e2063ddde6c9fa3398ca81484f27dec642c5627ffcaa0d4051d9"

UPSTREAM_CHECK_REGEX = "xdg-utils-(?P<pver>((\d+[\.\-_]*)+)((rc|alpha|beta)\d+)?)\.(tar\.gz|tgz)"

# Needs brokensep as this doesn't use automake
inherit autotools-brokensep features_check

# The xprop requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "xmlto-native libxslt-native"
RDEPENDS:${PN} += "xprop"
