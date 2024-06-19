SUMMARY = "Notes plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-notes-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "gtk+3 libxfce4ui xfce4-panel xfconf"

CFLAGS += "-Wno-error=incompatible-pointer-types"
SRC_URI[sha256sum] = "eb38246deb0fc89535fa9ff9b953c762cece232b5585d8210fab9abbf282aae3"

# Add /usr/share/xfce4/notes/gtk-3.0/gtk.css
FILES:${PN} += "${datadir}/xfce4/notes"
