SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE="GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    fontconfig \
    gtk+3 \
    gnome-desktop3 \
    gsettings-desktop-schemas \
    gnome-autoar \
    tracker \
"

inherit gnomebase gsettings gobject-introspection gtk-doc gettext features_check upstream-version-is-even mime-xdg

SRC_URI[archive.md5sum] = "c3c8dbb90d8eeed6c127aa568e131395"
SRC_URI[archive.sha256sum] = "b6cafc7ab1e70a64383de391b6097fcccbf36b208f8502d8c46423224fd30ef8"

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG = "extensions"
PACKAGECONFIG[extensions] = "-Dextensions=true,-Dextensions=false, gexiv2 gstreamer1.0-plugins-base"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"

# mandatory - not checked during configuration:
# | (org.gnome.Nautilus:863): GLib-GIO-ERROR **: 21:03:52.326: Settings schema 'org.freedesktop.Tracker.Miner.Files' is not installed
RDEPENDS_${PN} += "tracker-miners"
