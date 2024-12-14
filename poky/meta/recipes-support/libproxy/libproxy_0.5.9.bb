SUMMARY = "Library providing automatic proxy configuration management"
DESCRIPTION = "libproxy  provides  interfaces  to  get  the proxy that will be \
used to access network resources. It uses various plugins to get proxy \
configuration  via different mechanisms (e.g. environment variables or \
desktop settings)."
HOMEPAGE = "https://github.com/libproxy/libproxy"
BUGTRACKER = "https://github.com/libproxy/libproxy/issues"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://src/libproxy/proxy.c;beginline=1;endline=20;md5=bb9a177ef1c995311070f34c5638a402 \
                   "

DEPENDS = "glib-2.0"

SRC_URI = "git://github.com/libproxy/libproxy;protocol=https;branch=main"
SRCREV = "77e2a2b88a319974cf099c8eaaaa03030bc4c0d4"
S = "${WORKDIR}/git"

inherit meson pkgconfig gobject-introspection vala gi-docgen
GIDOCGEN_MESON_OPTION = 'docs'

PACKAGECONFIG ?= ""
PACKAGECONFIG[curl] = "-Dcurl=true,-Dcurl=false,curl"
PACKAGECONFIG[config-gnome] = "-Dconfig-gnome=true,-Dconfig-gnome=false,gsettings-desktop-schemas"
PACKAGECONFIG[pacrunner-duktape] = "-Dpacrunner-duktape=true,-Dpacrunner-duktape=false,duktape"

FILES:${PN} += "${libdir}/${BPN}/${PV}/modules"
