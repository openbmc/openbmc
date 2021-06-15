SUMMARY = "Tracker is a file search engine"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING.GPL;md5=ee31012bf90e7b8c108c69f197f3e3a4 \
    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
"

DEPENDS = " \
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

SRC_URI[archive.sha256sum] = "c120d81f2df94aa794b1a7953b55cfa46de318ed68e6f3b5ad4fce0c729c2483"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

# text search is not an option anymore and requires sqlite3 build with
# PACKAGECONFIG[fts5] set (default)

# set required cross property sqlite3_has_fts5
do_write_config[vardeps] += "PACKAGECONFIG"
do_write_config_append() {
    echo "[properties]" > ${WORKDIR}/meson-tracker.cross
    echo "sqlite3_has_fts5 = 'true'" >> ${WORKDIR}/meson-tracker.cross
}

EXTRA_OEMESON = " \
    --cross-file ${WORKDIR}/meson-tracker.cross \
    -Dman=false \
    -Dsystemd_user_services=${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)} \
    -Dsystemd_user_services_dir=${systemd_user_unitdir} \
"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/tracker3 \
    ${libdir}/tracker-3.0 \
    ${systemd_user_unitdir} \
"
