SUMMARY = "C Library for manipulating module metadata files"
HOMEPAGE = "https://github.com/fedora-modularity/libmodulemd"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https;branch=main"

PV = "2.13.0"
SRCREV = "1a032da198333ee77bdbe4be65e60eb4115ea73f"

S = "${WORKDIR}/git"

inherit meson gobject-introspection pkgconfig

EXTRA_OEMESON = "-Dwith_py3=false -Dwith_docs=false -Drpmio=disabled -Dlibmagic=disabled -Dwith_manpages=disabled -Dgobject_overrides_dir_py3=${PYTHON_SITEPACKAGES_DIR}/gi/overrides"

DEPENDS += "glib-2.0 libyaml glib-2.0-native"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'
