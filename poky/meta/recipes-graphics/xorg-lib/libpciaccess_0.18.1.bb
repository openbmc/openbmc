SUMMARY = "Generic PCI access library for X"

DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/libs"
LICENSE = "MIT"
DEPENDS = "util-macros"

SRC_URI = "${XORG_MIRROR}/individual/lib/${BP}.tar.xz"

inherit features_check pkgconfig meson

REQUIRED_DISTRO_FEATURES ?= "x11"

SRC_URI[sha256sum] = "4af43444b38adb5545d0ed1c2ce46d9608cc47b31c2387fc5181656765a6fa76"

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=54c978968e565218eea36cf03ef24352"

REQUIRED_DISTRO_FEATURES = ""

BBCLASSEXTEND = "native nativesdk"
