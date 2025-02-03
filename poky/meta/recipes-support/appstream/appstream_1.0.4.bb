SUMMARY = "AppStream is a collaborative effort for making machine-readable software metadata easily available."
HOMEPAGE = "https://github.com/ximion/appstream"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=435ed639f84d4585d93824e7da3d85da"

DEPENDS = " \
    appstream-native \
    curl-native \
    curl \
    docbook-xml-dtd4-native \
    gperf-native \
    glib-2.0 \
    libyaml \
    libxml2 \
    libxmlb \
    libxslt-native \
    itstool-native \
    docbook-xsl-stylesheets-native \
    python3-pygments-native \
"

inherit meson gobject-introspection gettext gi-docgen pkgconfig vala

GIR_MESON_OPTION = "gir"
GIDOCGEN_MESON_OPTION = "apidocs"

SRC_URI = " \
	https://www.freedesktop.org/software/appstream/releases/AppStream-${PV}.tar.xz \
	file://0001-remove-hardcoded-path.patch \
	file://0002-Do-not-build-qt-tests.patch \
	file://0003-Fix-PACKAGE_PREFIX_DIR-in-qt-cmake-AppStreamQtConfig.patch \
"
SRC_URI[sha256sum] = "2d218348067f98c4f34a3ffe2d8219ad9f504b1b2481f1872dec66c77bf37509"

S = "${WORKDIR}/AppStream-${PV}"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[stemming] = "-Dstemming=true,-Dstemming=false,libstemmer"
PACKAGECONFIG[qt6] = "-Dqt=true,-Dqt=false,qtbase"

FILES:${PN} += "${datadir}"

EXTRA_OEMESON += "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)}"

BBCLASSEXTEND = "native"

# Fix meson not finding the Qt build tools in cross-compilation
# setups. See: https://github.com/mesonbuild/meson/issues/13018
do_configure:prepend:class-target() {
    export PATH=${STAGING_DIR_NATIVE}${libexecdir}:$PATH
}
