SUMMARY = "discover and manipulate UPNP/DLNA media renderers"
HOMEPAGE = "https://01.org/dleyna/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://src/renderer-service-upnp.c;beginline=1;endline=21;md5=c25c3082684eb4ca87474b7528c6dc15"

PNBLACKLIST[renderer-service-upnp] ?= "BROKEN: doesn't build with B!=S (trying to install rendererconsole.py from ${B} instead of ${S})"

DEPENDS = "dbus glib-2.0 gssdp gupnp gupnp-av gupnp-dlna libsoup-2.4"

SRC_URI = "git://github.com/01org/${BPN}.git"
SRCREV = "4a0b1d7cd8e22d3cb0e09c77c344ceccbcbbd34f"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

do_install_append() {
    install -d ${D}${bindir}
    install -m 0755 test/rendererconsole.py ${D}${bindir}
}

PACKAGES =+ "${PN}-tests"

RDEPENDS_${PN}-tests = "python-dbus python-json python-misc python-pkgutil python-xml"

FILES_${PN} += "${datadir}/dbus-1/services/*.service"
# When we have GI, package cap
FILES_${PN}-tests = "${bindir}/rendererconsole.py"
