SUMMARY = "C Library for manipulating module metadata files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https \
           file://0001-modulemd-generate-the-manpage-only-if-the-feature-is.patch \
           file://0002-meson.build-do-not-install-python-gi-bindings.patch \
           "

PV = "2.9.2"
SRCREV = "780750eab8a0587b20d033f335301f16d00ab9c9"

S = "${WORKDIR}/git"

inherit meson gobject-introspection

EXTRA_OEMESON = "-Ddeveloper_build=false -Dwith_docs=false -Drpmio=disabled -Dlibmagic=disabled -Dwith_manpages=disabled"

DEPENDS += "glib-2.0 libyaml glib-2.0-native python3"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'
