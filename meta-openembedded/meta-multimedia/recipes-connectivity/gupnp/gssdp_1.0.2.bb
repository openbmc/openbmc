SUMMARY = "Resource discovery and announcement over SSDP"
DESCRIPTION = "GSSDP implements resource discovery and announcement over SSDP (Simpe Service Discovery Protocol)."
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"
DEPENDS = "glib-2.0 libsoup-2.4"

SRC_URI = "${GNOME_MIRROR}/${BPN}/1.0/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "b30c9a406853c6a3a012d151d6e7ad2c"
SRC_URI[sha256sum] = "a1e17c09c7e1a185b0bd84fd6ff3794045a3cd729b707c23e422ff66471535dc"

inherit autotools pkgconfig gobject-introspection vala gtk-doc

# Copy vapigen.m4 so that it doesn't get removed by vala class
# (normally this would be the right thing to do, but in gssdp the vapigen.m4 has only a custom macro)
do_configure_prepend() {
        cp -f ${S}/m4/vapigen.m4 ${S}/m4/vapigen-custom.m4 || true
}

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk', '', d)}"
PACKAGECONFIG[gtk] = "--with-gtk,--without-gtk,gtk+3"

PACKAGES =+ "gssdp-tools"

FILES_gssdp-tools = "${bindir}/gssdp* ${datadir}/gssdp/*.glade"
