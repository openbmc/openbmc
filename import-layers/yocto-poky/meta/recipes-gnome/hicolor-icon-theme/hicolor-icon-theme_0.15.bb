SUMMARY = "Default icon theme that all icon themes automatically inherit from"
HOMEPAGE = "http://icon-theme.freedesktop.org/wiki/HicolorTheme"
BUGTRACKER = "https://bugs.freedesktop.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f08a446809913fc9b3c718f0eaea0426"

SRC_URI = "http://icon-theme.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "6aa2b3993a883d85017c7cc0cfc0fb73"
SRC_URI[sha256sum] = "9cc45ac3318c31212ea2d8cb99e64020732393ee7630fa6c1810af5f987033cc"

inherit allarch autotools

FILES_${PN} += "${datadir}/icons"
