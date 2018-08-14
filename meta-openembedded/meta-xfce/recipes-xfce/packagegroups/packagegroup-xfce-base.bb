SUMMARY = "All packages required for a base installation of XFCE"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
PR = "r5"

inherit packagegroup distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} = " \
    xfwm4 \
    xfce4-session \
    xfconf \
    xfdesktop \
    xfce4-panel \
    \
    gtk-xfce-engine \
    librsvg-gtk \
    \
    xfce4-panel-plugin-actions \
    xfce4-panel-plugin-applicationsmenu \
    xfce4-panel-plugin-clock \
    xfce4-panel-plugin-directorymenu \
    xfce4-panel-plugin-launcher \
    xfce4-panel-plugin-pager \
    xfce4-panel-plugin-separator \
    xfce4-panel-plugin-showdesktop \
    xfce4-panel-plugin-systray \
    xfce4-panel-plugin-tasklist \
    xfce4-panel-plugin-windowmenu \
    xfce4-settings \
    \
    xfce4-notifyd \
    xfce4-terminal \
    thunar \
    thunar-volman \
"
