SUMMARY = "C Library for manipulating module metadata files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https;branch=main \
           file://0001-modulemd-generate-the-manpage-only-if-the-feature-is.patch \
           "

PV = "2.12.0"
SRCREV = "aaed38c899a958a88a6a368a70510e350ebe336c"

S = "${WORKDIR}/git"

inherit meson gobject-introspection python3-dir

EXTRA_OEMESON = "-Dwith_docs=false -Drpmio=disabled -Dlibmagic=disabled -Dwith_manpages=disabled -Dgobject_overrides_dir_py3=${PYTHON_SITEPACKAGES_DIR}/gi/overrides"

DEPENDS += "glib-2.0 libyaml glib-2.0-native python3"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'

FILES_${PN} += "${libdir}/${PYTHON_DIR}/*"
