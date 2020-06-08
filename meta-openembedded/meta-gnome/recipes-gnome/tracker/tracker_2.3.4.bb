SUMMARY = "Tracker is a file search engine"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING.GPL;md5=ee31012bf90e7b8c108c69f197f3e3a4 \
    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
"

DEPENDS = " \
    libxml2-native \
    dbus-native \
    glib-2.0 \
    sqlite3 \
    libarchive \
    dbus \
    icu \
    json-glib \
    libsoup-2.4 \
    libstemmer \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection vala gtk-doc manpages bash-completion features_check

SRC_URI[archive.md5sum] = "9937cf4812ca85078907324c7cdf7dd8"
SRC_URI[archive.sha256sum] = "577952244ab977c78b0b88e2f63c4197eaba16e4d66bff692b7f58993e06516d"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

PACKAGECONFIG ??= "fts"
PACKAGECONFIG[networkmanager] = "-Dnetwork_manager=enabled,-Dnetwork_manager=disabled,networkmanger"
# full text search requires sqlite3 build with PACKAGECONFIG[fts5] set
PACKAGECONFIG[fts] = "-Dfts=true,-Dfts=false"

# set required cross property sqlite3_has_fts5
do_write_config[vardeps] += "PACKAGECONFIG"
do_write_config_append() {
    echo "[properties]" > ${WORKDIR}/meson-tracker.cross
    echo "sqlite3_has_fts5 = '${@bb.utils.contains('PACKAGECONFIG', 'fts', 'true', 'false', d)}'" >> ${WORKDIR}/meson-tracker.cross
}

EXTRA_OEMESON = "--cross-file ${WORKDIR}/meson-tracker.cross"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/tracker-2.0 \
    ${systemd_user_unitdir} \
"
