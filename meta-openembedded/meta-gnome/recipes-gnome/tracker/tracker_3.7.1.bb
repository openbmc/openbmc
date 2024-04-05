SUMMARY = "Tracker is a file search engine"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING.GPL;md5=ee31012bf90e7b8c108c69f197f3e3a4 \
    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
"

DEPENDS = " \
    dbus-native \
    python3-pygobject-native \
    glib-2.0 \
    sqlite3 \
    libarchive \
    dbus \
    icu \
    json-glib \
    libsoup-3.0 \
    libstemmer \
"


inherit gettext gnomebase gobject-introspection vala gtk-doc bash-completion python3native

SRC_URI += "file://0001-fix-reproducibility.patch"
SRC_URI[archive.sha256sum] = "cd91b885ee9839cde5387f8e05ed24a7108d1627629cb6b1b2aa51a9603e130a"

# text search is not an option anymore and requires sqlite3 build with
# PACKAGECONFIG[fts5] set (default)

# set required cross property sqlite3_has_fts5
do_write_config[vardeps] += "PACKAGECONFIG"
do_write_config:append() {
    echo "[properties]" > ${WORKDIR}/meson-tracker.cross
    echo "sqlite3_has_fts5 = 'true'" >> ${WORKDIR}/meson-tracker.cross
}

EXTRA_OEMESON = " \
    --cross-file ${WORKDIR}/meson-tracker.cross \
    -Dman=false \
    -Dsystemd_user_services=${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)} \
    -Dsystemd_user_services_dir=${systemd_user_unitdir} \
    -Dsoup=soup3 \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=enabled', '-Dvapi=disabled', d)} \
"

do_install:prepend() {
    sed -i -e 's|${B}/../${PN}-${PV}|${TARGET_DBGSRC_DIR}|g' ${B}/src/libtracker-sparql/tracker-sparql-enum-types.c
    sed -i -e 's|${B}/../${PN}-${PV}|${TARGET_DBGSRC_DIR}|g' ${B}/src/libtracker-sparql/core/tracker-data-enum-types.c
}

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/tracker3 \
    ${libdir}/tracker-3.0 \
    ${systemd_user_unitdir} \
"
