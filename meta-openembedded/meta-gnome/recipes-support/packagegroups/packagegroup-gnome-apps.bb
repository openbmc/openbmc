SUMMARY = "GNOME applications"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} = " \
    evolution-data-server \
    evince \
    file-roller \
    gedit \
    ghex \
    gnome-calculator \
    gnome-calendar \
    gnome-disk-utility \
    gnome-font-viewer \
    gnome-photos \
    gnome-terminal \
    libwnck3 \
    nautilus \
    ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'gnome-system-monitor', '', d)} \
"
