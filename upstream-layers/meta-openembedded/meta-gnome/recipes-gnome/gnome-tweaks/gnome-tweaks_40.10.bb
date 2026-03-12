SUMMARY = "GNOME tweaks: Advanced options for GNOME 3 session"
LICENSE = "GPL-3.0-only & CC0-1.0"
LIC_FILES_CHKSUM = " \
    file://LICENSES/CC0-1.0;md5=65d3616852dbf7b1a6d4b53b00626032 \
    file://LICENSES/GPL-3.0;md5=9eef91148a9b14ec7f9df333daebc746 \
"

DEPENDS = "libhandy"


inherit gnomebase gtk-icon-cache gobject-introspection features_check

# same as gnome-shell
REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"
GIR_MESON_OPTION = ""

SRC_URI[archive.sha256sum] = "cffd3b33a399577fbb4491ce8fbd02f54c5924f1056f30114e8d420290ef23fc"
SRC_URI += " \
    file://0001-Make-python-path-configurable.patch \
"

EXTRA_OEMESON = "-Dpython_site_dir=${PYTHON_SITEPACKAGES_DIR}"

# The optimised .pyc files contain buildpaths, delete them and leave the
# normal ones to be packaged.
do_install:append() {
    find ${D}${PYTHON_SITEPACKAGES_DIR} -name *.opt*.pyc -delete
}

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${PYTHON_SITEPACKAGES_DIR} \
"

RDEPENDS:${PN} += "gnome-shell python3-core python3-logging libhandy"
