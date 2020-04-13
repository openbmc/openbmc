SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+3 gtksourceview3 xfconf xfce4-dev-tools-native"

inherit xfce-app gsettings mime-xdg

SRC_URI[md5sum] = "98d908842d4a93c35756a67d681c08fe"
SRC_URI[sha256sum] = "84c02adfca7f8b33b9466a306ded72fb9f38f93c9edb78660343854c4a3aded7"

FILES_${PN} += " \
    ${datadir}/glib-2.0/schemas \
    ${datadir}/polkit-1 \
"
