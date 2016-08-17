SUMMARY = "GNOME panel"
LICENSE = "GPLv2 & LGPLv2 & GFDL-1.1"

BPN = "gnome-panel"
PR = "r1"

PNBLACKLIST[gnome-panel3] ?= "CONFLICT: depends on libgweather3 which conflicts with libgweather"

# conflicts with gnome-panel, because they provide the same package
# http://patches.openembedded.org/patch/43105/
EXCLUDE_FROM_WORLD = "1"
DEFAULT_PREFERENCE = "-1"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-DOCS;md5=c9211dab3ae61e580f48432020784324 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SECTION = "x11/gnome"

DEPENDS = "gnome-doc-utils-native gtk+3 dconf gconf glib-2.0 gnome-desktop3 gtk+ pango libwnck3 gnome-menus cairo libgweather3 dbus-glib librsvg libcanberra" 

inherit gtk-doc gnome gettext pkgconfig

SRC_URI += "file://as-needed.patch "

SRC_URI[archive.md5sum] = "0f2f644dc4081b72f6df7a65282af7c6"
SRC_URI[archive.sha256sum] = "25db8ec026c4bf47f0ef5cc7e2712f2aad175bd7fb8e4952ef5f8b200f17f196"

EXTRA_OECONF = "--disable-scrollkeeper --disable-eds --enable-bonobo=no --with-in-process-applets=none"

do_configure_prepend() {
    gnome-doc-prepare --automake
    sed -i -e s:help:: ${S}/Makefile.am
}

pkg_postinst_${PN}_append () {
    gconftool-2 --config-source=xml:readwrite:/etc/gconf/gconf.xml.defaults \
        --direct --load /etc/gconf/schemas/panel-default-setup.entries
}

PACKAGES =+ "libpanel-applet"
FILES_libpanel-applet = "${libdir}/libpanel-applet-3.so.*"

FILES_${PN} =+ "${datadir}/gnome* \
                ${datadir}/dbus-1 \
                ${datadir}/icons \
                ${datadir}/PolicyKit \
                ${libdir}/bonobo \
"


