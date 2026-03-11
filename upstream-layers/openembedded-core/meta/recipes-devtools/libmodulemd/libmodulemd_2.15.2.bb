SUMMARY = "C Library for manipulating module metadata files"
HOMEPAGE = "https://github.com/fedora-modularity/libmodulemd"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https;branch=main;tag=${PV}"

SRCREV = "b8b11b4dafaa2c4d73883152bfa7e5bd81cd7395"

inherit meson gobject-introspection pkgconfig manpages

EXTRA_OEMESON = "-Dwith_py3=false -Dwith_docs=false -Drpmio=disabled -Dgobject_overrides_dir_py3=${PYTHON_SITEPACKAGES_DIR}/gi/overrides"

DEPENDS += "glib-2.0 libyaml glib-2.0-native"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "-Dwith_manpages=enabled,-Dwith_manpages=disabled"
