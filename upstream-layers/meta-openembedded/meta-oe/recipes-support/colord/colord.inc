SUMMARY = "Making color management just work"
HOMEPAGE = "https://www.freedesktop.org/software/colord/"
LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://meson.build;beginline=3;endline=3;md5=f42198707d793be58b274d34fd5238c3 \
"

PV = "1.4.8"
SRC_URI = "https://www.freedesktop.org/software/colord/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "21500bd68975312a7f0f3ce6019d9f75f42aacaa75ca7115ec720b5445406896"

