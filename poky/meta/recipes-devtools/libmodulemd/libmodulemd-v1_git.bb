SUMMARY = "C Library for manipulating module metadata files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https;branch=1.x-maint \
           file://0001-spec_tmpl.sh-use-bin-sh-not-usr-bin-sh.patch \
           "

PV = "1.8.16"
SRCREV = "d0dcf7b373b3cf85cd39eb3bc23d31e06195a75a"
UPSTREAM_CHECK_GITTAGREGEX = "libmodulemd-(?P<pver>1.*\d)"

S = "${WORKDIR}/git"

inherit meson gobject-introspection

EXTRA_OEMESON = "-Ddeveloper_build=false -Dwith_docs=false"

DEPENDS += "glib-2.0 libyaml glib-2.0-native python3"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'
