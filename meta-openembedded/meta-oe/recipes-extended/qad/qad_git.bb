SUMMARY = "Simple, REST-API compliant daemon for automated testing"
DESCRIPTION = " This is a simple, REST-API compliant daemon which makes \
automated testing on hardware possible by removing the need for physical \
intervention as Q.A.D allows inputs to be injected via http/https requests. \
This both eliminates the need to physically interact with the rig and allows \
for tasks to be carried out entirely automatically."

LICENSE = "MIT & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=402cce7fbcb6ea9ab5a0378dd7f40806 \
                    file://openqa/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                   "
SRC_URI = "git://gitlab.com/CodethinkLabs/qad/qad;branch=main;protocol=https \
           file://0001-Fix-warnings-found-by-clang-compiler.patch"

SRCREV = "ae0c099c1fdc0ca6f5d631cea6b302937122b362"

S = "${WORKDIR}/git"
PV = "0.0+git${SRCPV}"

DEPENDS = "cjson libmicrohttpd libdrm libpng"

FILES:${PN} += "qad"
inherit meson pkgconfig

do_install () {
    install -d ${D}${bindir}
    install -p -m 755 qad ${D}${bindir}/
}
