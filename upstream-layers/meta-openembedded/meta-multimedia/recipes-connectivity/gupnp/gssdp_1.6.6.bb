SUMMARY = "Resource discovery and announcement over SSDP"
DESCRIPTION = "GSSDP implements resource discovery and announcement over SSDP \
               (Simpe Service Discovery Protocol)."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gssdp/"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gssdp/-/issues"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase pkgconfig gobject-introspection vala gi-docgen features_check ptest
SRC_URI += "file://run-ptest"
SRC_URI[archive.sha256sum] = "767d2275254ce0efeaeac64419ff9f4f0ad470d134ef672f5c556b2abb786bcb"

DEPENDS = " \
    glib-2.0 \
    libsoup-3.0 \
"


# manpages require pandoc-native
EXTRA_OEMESON += "-Dmanpages=false"

SNIFFER = "${@bb.utils.contains("BBFILE_COLLECTIONS", "gnome-layer", "sniffer", "", d)}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', "${SNIFFER}", "", d)}"
PACKAGECONFIG[sniffer] = "-Dsniffer=true,-Dsniffer=false,gtk4,"

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'sniffer', 'opengl', '', d)}"

PACKAGES =+ "gssdp-tools"

do_install_ptest(){
    install -d ${D}${PTEST_PATH}/tests
    find ${B}/tests -type f -executable -exec install {} ${D}${PTEST_PATH}/tests \;
}

FILES:gssdp-tools = "${bindir}/gssdp* ${datadir}/gssdp/*.glade"

