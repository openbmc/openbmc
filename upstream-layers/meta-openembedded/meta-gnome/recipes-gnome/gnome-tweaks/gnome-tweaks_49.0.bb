SUMMARY = "GNOME tweaks: Advanced options for GNOME 3 session"
LICENSE = "CC0-1.0 AND GPL-3.0-only"
LIC_FILES_CHKSUM = " \
    file://LICENSES/CC0-1.0;md5=65d3616852dbf7b1a6d4b53b00626032 \
    file://LICENSES/GPL-3.0;md5=9eef91148a9b14ec7f9df333daebc746 \
"

DEPENDS = "glib-2.0 gtk4 libadwaita libgudev python3-pygobject gsettings-desktop-schemas"

inherit gnomebase gtk-icon-cache gobject-introspection features_check

# same as gnome-shell
REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"
GIR_MESON_OPTION = ""

SRC_URI[archive.sha256sum] = "b3909bdcb4905b68427d6ab581e01f436dff8e5c0a389b1e0b14500f18806ebb"

# The optimised .pyc files contain buildpaths, delete them and leave the
# normal ones to be packaged.
do_install:append() {
    find ${D}${PYTHON_SITEPACKAGES_DIR} -name *.opt*.pyc -delete
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${PYTHON_SITEPACKAGES_DIR} \
"

RDEPENDS:${PN} += "gnome-shell python3-core python3-logging python3-pygobject gtk4 libadwaita gnome-desktop libgudev libnotify"
