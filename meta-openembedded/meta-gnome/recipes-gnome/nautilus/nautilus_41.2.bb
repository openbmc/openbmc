SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE="GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    fontconfig \
    gtk+3 \
    gnome-desktop \
    gsettings-desktop-schemas \
    gnome-autoar \
    tracker \
    libhandy \
    libportal \
"

inherit gnomebase gsettings gobject-introspection gtk-doc gettext features_check mime-xdg

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI += "file://liportal-0.5-fix.patch"
SRC_URI[archive.sha256sum] = "872285b1c40a6ee418ce295ed115f5427da70907d822d95bcf51675d5498822b"

REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OEMESON += " \
    -Dtests=none \
"

PACKAGECONFIG = "extensions"
PACKAGECONFIG[extensions] = "-Dextensions=true,-Dextensions=false, gexiv2 gstreamer1.0-plugins-base"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
    ${datadir}/tracker3 \
"

# mandatory - not checked during configuration:
# | (org.gnome.Nautilus:863): GLib-GIO-ERROR **: 21:03:52.326: Settings schema 'org.freedesktop.Tracker.Miner.Files' is not installed
RDEPENDS:${PN} += "tracker-miners"
