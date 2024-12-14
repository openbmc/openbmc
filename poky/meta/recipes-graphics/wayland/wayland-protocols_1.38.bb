SUMMARY = "Collection of additional Wayland protocols"
DESCRIPTION = "Wayland protocols that add functionality not \
available in the Wayland core protocol. Such protocols either add \
completely new functionality, or extend the functionality of some other \
protocol either in Wayland core, or some other protocol in \
wayland-protocols."
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c7b12b6702da38ca028ace54aae3d484 \
                    file://stable/presentation-time/presentation-time.xml;endline=26;md5=4646cd7d9edc9fa55db941f2d3a7dc53"

SRC_URI = "https://gitlab.freedesktop.org/wayland/wayland-protocols/-/releases/${PV}/downloads/wayland-protocols-${PV}.tar.xz"
SRC_URI[sha256sum] = "ff17292c05159d2b20ce6cacfe42d7e31a28198fa1429a769b03af7c38581dbe"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/wayland/wayland-protocols/-/tags"
UPSTREAM_CHECK_REGEX = "releases/(?P<pver>.+)"

DEPENDS += "wayland-native"

inherit meson pkgconfig allarch

EXTRA_OEMESON += "-Dtests=false"

FILES:${PN} += "${datadir}/pkgconfig/wayland-protocols.pc"

BBCLASSEXTEND = "native nativesdk"

