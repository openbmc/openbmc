SUMMARY = "Notes plugin for the Xfce Panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-notes-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "gtk+3 libxfce4ui xfce4-panel xfconf gtksourceview4"

CFLAGS += "-Wno-error=incompatible-pointer-types"
SRC_URI[sha256sum] = "8301fcd397bbc98a3def3d94f04de30cc128b4a35477024d2bcb2952a161a3b5"

# Add /usr/share/xfce4/notes/gtk-3.0/gtk.css
FILES:${PN} += "${datadir}/xfce4/notes"
