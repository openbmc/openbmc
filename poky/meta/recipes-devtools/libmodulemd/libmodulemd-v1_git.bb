SUMMARY = "C Library for manipulating module metadata files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a3927bff3ee4f5b21bcb0ed3fcd6bb"

SRC_URI = "git://github.com/fedora-modularity/libmodulemd;protocol=https;branch=1.x-maint \
           file://0001-spec_tmpl.sh-use-bin-sh-not-usr-bin-sh.patch \
           file://0001-Do-not-generate-gtkdoc-or-python-bindings.patch \
           "

PV = "1.8.15"
SRCREV = "2d461725f781c6fdcf32893d8dcfa40bcef8dda5"
UPSTREAM_CHECK_GITTAGREGEX = "libmodulemd-(?P<pver>1.*\d)"

S = "${WORKDIR}/git"

inherit meson gobject-introspection

EXTRA_OEMESON = "-Ddeveloper_build=false"

DEPENDS += "glib-2.0 libyaml glib-2.0-native python3"

BBCLASSEXTEND = "native nativesdk"

GIR_MESON_OPTION = 'skip_introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'
