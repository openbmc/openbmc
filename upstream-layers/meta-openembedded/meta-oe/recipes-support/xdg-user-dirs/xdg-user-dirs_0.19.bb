DESCRIPTION = "xdg-user-dirs is a tool to help manage user directories like the desktop folder and the music folder"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://user-dirs.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "e92deb929c10d4b29329397af8a2585101247f7e6177ac6f1d28e82130ed8c19"

inherit autotools gettext pkgconfig

EXTRA_OECONF = "--disable-documentation"

CONFFILES:${PN} += " \
    ${sysconfdir}/xdg/user-dirs.conf \
    ${sysconfdir}/xdg/user-dirs.defaults \
"

FILES:${PN} += "${systemd_user_unitdir}/xdg-user-dirs.service"
