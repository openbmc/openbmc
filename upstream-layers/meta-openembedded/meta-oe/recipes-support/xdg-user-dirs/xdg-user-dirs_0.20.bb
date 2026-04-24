DESCRIPTION = "xdg-user-dirs is a tool to help manage user directories like the desktop folder and the music folder"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://user-dirs.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "b8e34286278f4fef3e1bfe9685c395ccc0eb50c14d3a2fb4953dd00fbfd3af39"

inherit meson gettext pkgconfig

EXTRA_OEMESON = "-Ddocs=false"

CONFFILES:${PN} += " \
    ${sysconfdir}/xdg/user-dirs.conf \
    ${sysconfdir}/xdg/user-dirs.defaults \
"

FILES:${PN} += "${systemd_user_unitdir}/xdg-user-dirs.service"
