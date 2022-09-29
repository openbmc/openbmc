DESCRIPTION = "xdg-user-dirs is a tool to help manage user directories like the desktop folder and the music folder"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://user-dirs.freedesktop.org/releases/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "ec6f06d7495cdba37a732039f9b5e1578bcb296576fde0da40edb2f52220df3c"

inherit autotools gettext

EXTRA_OECONF = "--disable-documentation"

CONFFILES:${PN} += " \
    ${sysconfdir}/xdg/user-dirs.conf \
    ${sysconfdir}/xdg/user-dirs.defaults \
"
