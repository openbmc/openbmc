SUMMARY = "GNOME tweaks: Advanced options for GNOME 3 session"
LICENSE = "GPLv3 & CC0-1.0"
LIC_FILES_CHKSUM = " \
    file://LICENSES/CC0-1.0;md5=65d3616852dbf7b1a6d4b53b00626032 \
    file://LICENSES/GPL-3.0;md5=9eef91148a9b14ec7f9df333daebc746 \
"

DEPENDS = "libhandy"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-icon-cache gobject-introspection features_check upstream-version-is-even

# same as gnome-shell
REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

SRC_URI[archive.md5sum] = "a625d8b167c5549c68e1c6ac7a87d369"
SRC_URI[archive.sha256sum] = "003326fab46e6faad9485924bca503f0c583e3b4553d6f673406eda396205250"
SRC_URI += "file://0001-Make-python-path-configurable.patch"

EXTRA_OEMESON = "-Dpython_site_dir=${PYTHON_SITEPACKAGES_DIR}"

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${PYTHON_SITEPACKAGES_DIR} \
"

RDEPENDS_${PN} += "gnome-shell python3-core python3-logging libhandy"
