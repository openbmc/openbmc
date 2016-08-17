SUMMARY = "Resource discovery and announcement over SSDP"
DESCRIPTION = "GSSDP implements resource discovery and announcement over SSDP (Simpe Service Discovery Protocol)."
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"
DEPENDS = "glib-2.0 libsoup-2.4"

inherit autotools pkgconfig gobject-introspection vala

# Copy vapigen.m4 so that it doesn't get removed by vala class
# (normally this would be the right thing to do, but in gssdp the vapigen.m4 has only a custom macro)
do_configure_prepend() {
        cp -f ${S}/m4/vapigen.m4 ${S}/m4/vapigen-custom.m4 || true
}

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk', '', d)}"
PACKAGECONFIG[gtk] = "--with-gtk,--without-gtk,gtk+"

PACKAGES =+ "gssdp-tools"

FILES_gssdp-tools = "${bindir}/gssdp* ${datadir}/gssdp/*.glade"
