SUMMARY = "Making color management just work"
HOMEPAGE = "https://www.freedesktop.org/software/colord/"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://meson.build;beginline=3;endline=3;md5=f42198707d793be58b274d34fd5238c3 \
"

PV = "1.4.7"
SRC_URI = "https://www.freedesktop.org/software/colord/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "de02d9910634ae159547585cec414e450f711c27235453b4f9b38a9f2361a653"

