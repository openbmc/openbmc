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
"
SRC_URI[sha256sum] = "1a5148ca97dcbf5eb6e9c380278bb0d20938569292ea8652df1b3cac8bd2736b"

S = "${WORKDIR}/AppStream-${PV}"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[stemming] = "-Dstemming=true,-Dstemming=false,libstemmer"

FILES:${PN} += "${datadir}"

EXTRA_OEMESON += "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)}"

BBCLASSEXTEND = "native"
