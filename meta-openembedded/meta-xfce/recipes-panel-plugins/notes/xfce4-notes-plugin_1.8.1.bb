SUMMARY = "Notes plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-notes-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

DEPENDS += "gtk+ libxfce4ui xfce4-panel xfconf libunique"

SRC_URI += " \
    file://0001-main-status-icon.c-remove-deprecated-g_type_init.patch \
    file://01_fix_format-string.patch \
"
SRC_URI[md5sum] = "31cb9520b01512a94344770b4befdb3b"
SRC_URI[sha256sum] = "07a4c3e71431c24f97d2e270452dd0fa51ff0bdb6219a13a20d0bfa8d9de54b2"

FILES_${PN} += "${libdir}/xfce4/panel-plugins/*.so.*"

# *.so are required for plugin detection
INSANE_SKIP_${PN} = "dev-so"
