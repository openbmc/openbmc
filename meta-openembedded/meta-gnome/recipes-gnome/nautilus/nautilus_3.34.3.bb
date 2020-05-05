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

SRC_URI[archive.md5sum] = "769053f14bb993605a2b60085c47b4cd"
SRC_URI[archive.sha256sum] = "02d984b794f3a34a7aa66bf5c70cc2a7780de55bd900504fb1014931781db36d"

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
