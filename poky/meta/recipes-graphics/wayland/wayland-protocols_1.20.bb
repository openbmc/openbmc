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

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           "
SRC_URI[md5sum] = "b0836533a3f2dc6585b1dae00341157f"
SRC_URI[sha256sum] = "9782b7a1a863d82d7c92478497d13c758f52e7da4f197aa16443f73de77e4de7"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit allarch autotools pkgconfig

PACKAGES = "${PN}"
FILES_${PN} += "${datadir}/pkgconfig/wayland-protocols.pc"
