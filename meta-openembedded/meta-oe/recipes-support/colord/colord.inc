SUMMARY = "Making color management just work"
HOMEPAGE = "https://www.freedesktop.org/software/colord/"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://meson.build;beginline=3;endline=3;md5=f42198707d793be58b274d34fd5238c3 \
"

PV = "1.4.6"
SRC_URI = "https://www.freedesktop.org/software/colord/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "7407631a27bfe5d1b672e7ae42777001c105d860b7b7392283c8c6300de88e6f"

