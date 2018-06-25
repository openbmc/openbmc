SUMMARY = "Default icon theme that all icon themes automatically inherit from"
HOMEPAGE = "http://icon-theme.freedesktop.org/wiki/HicolorTheme"
BUGTRACKER = "https://bugs.freedesktop.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f08a446809913fc9b3c718f0eaea0426"

SRC_URI = "http://icon-theme.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "84eec8d6f810240a069c731f1870b474"
SRC_URI[sha256sum] = "317484352271d18cbbcfac3868eab798d67fff1b8402e740baa6ff41d588a9d8"

inherit allarch autotools

FILES_${PN} += "${datadir}/icons"
