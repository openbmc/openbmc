SUMMARY = "C Library for manipulating module metadata files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https \
           file://0001-spec_tmpl.sh-use-bin-sh-not-usr-bin-sh.patch \
           file://0002-modulemd-v1-meson.build-do-not-generate-gir-or-gtkdo.patch \
           "

PV = "2.1.0"
SRCREV = "072e6ee791fe7822a6d423bdac7e4a5cbb118bce"

S = "${WORKDIR}/git"

inherit meson gobject-introspection

EXTRA_OEMESON = "-Ddeveloper_build=false -Dbuild_api_v1=true -Dbuild_api_v2=false"

DEPENDS += "glib-2.0 libyaml glib-2.0-native"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'
