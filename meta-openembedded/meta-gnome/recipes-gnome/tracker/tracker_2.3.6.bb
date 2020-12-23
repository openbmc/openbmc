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

SRC_URI[archive.sha256sum] = "bd1eb4122135296fa7b57b1c3fa0ed602cf7d06c0b8e534d0bd17ff5f97feef2"

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

EXTRA_OEMESON = "--cross-file ${WORKDIR}/meson-tracker.cross \
                 -Dsystemd_user_services=${systemd_user_unitdir} \
"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/tracker-2.0 \
    ${systemd_user_unitdir} \
"
