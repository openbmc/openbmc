SUMMARY = "GNOME panel"
LICENSE = "GPL-2.0 & LGPL-2.0 & GFDL-1.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-DOCS;md5=c9211dab3ae61e580f48432020784324 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SECTION = "x11/gnome"

PR = "r7"

DEPENDS = "gnome-doc-utils-native gconf glib-2.0 gnome-desktop gtk+ \
           pango libwnck gnome-menus cairo libgweather dbus dbus-glib \
	   librsvg libcanberra"
RDEPENDS_${PN} = "python"

inherit gtk-doc gnome autotools-brokensep gettext pkgconfig gconf gobject-introspection

SRCREV = "8292bd2b8a36df7eed3c760899400790cde68590"
SRC_URI = "git://git.gnome.org/gnome-panel;branch=gnome-2-32 \
           file://0001-Fix-build-with-gcc-5.patch \
	  "

S = "${WORKDIR}/git"

EXTRA_OECONF = "--disable-scrollkeeper --disable-eds --enable-bonobo=no --with-in-process-applets=none"

PACKAGECONFIG ??= ""
PACKAGECONFIG[networkmanager] = "--enable-network-manager,--disable-network-manager,networkmanager"

do_configure_prepend() {
    gnome-doc-prepare --automake
    sed -i -e s:help:: ${S}/Makefile.am
    sed -i -e s:^#!@PYTHON@:#!/usr/bin/python: ${S}/gnome-panel/gnome-panel-add.in
}

PACKAGES =+ "libpanel-applet"
FILES_libpanel-applet = "${libdir}/libpanel-applet-*.so.*"

FILES_${PN} =+ "${datadir}/gnome* \
                ${datadir}/dbus-1 \
                ${datadir}/icons \
                ${datadir}/PolicyKit \
                ${libdir}/bonobo \
"
