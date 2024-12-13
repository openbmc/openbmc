DESCRIPTION = "This repository contains a number of commandline utilities for use inside Flatpak sandboxes."
HOMEPAGE = "http://flatpak.org"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/flatpak/flatpak-xdg-utils.git;protocol=https;branch=main"

SRCREV = "05abdd7421688be5835a6b12f2b068086c38d4aa"

S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS = "glib-2.0"
