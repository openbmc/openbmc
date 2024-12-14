SUMMARY = "Default icon theme that all icon themes automatically inherit from"
DESCRIPTION = "The hicolor-icon-theme package contains a default fallback \
theme for implementations of the icon theme specification."
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/icon-theme"
BUGTRACKER = "https://bugs.freedesktop.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f08a446809913fc9b3c718f0eaea0426"

SRC_URI = "https://icon-theme.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "db0e50a80aa3bf64bb45cbca5cf9f75efd9348cf2ac690b907435238c3cf81d7"

inherit allarch meson

FILES:${PN} += "${datadir}/icons"

BBCLASSEXTEND = "native nativesdk"
