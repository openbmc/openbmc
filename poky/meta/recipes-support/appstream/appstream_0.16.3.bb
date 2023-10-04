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

inherit meson gobject-introspection gettext gtk-doc pkgconfig vala

GIR_MESON_OPTION = "gir"
GTKDOC_MESON_OPTION = "apidocs"

SRC_URI = " \
	https://www.freedesktop.org/software/appstream/releases/AppStream-${PV}.tar.xz \
	file://0001-remove-hardcoded-path.patch \
	file://0001-meson-do-not-rely-on-an-exe-wrapper.patch \
"
SRC_URI[sha256sum] = "081c917646e94d7221c9e4aae54dacda95a27c607fa93cd8e6344a2b318b98b1"

S = "${WORKDIR}/AppStream-${PV}"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[stemming] = "-Dstemming=true,-Dstemming=false,libstemmer"

FILES:${PN} += "${datadir}"

EXTRA_OEMESON += "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)}"

BBCLASSEXTEND = "native"
