SUMMARY = "Tools for managing the libosinfo database files"
HOMEPAGE = "https://libosinfo.org"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "glib-2.0 json-glib libarchive libsoup-2.4"

SRC_URI = "git://gitlab.com/libosinfo/osinfo-db-tools.git;branch=main;protocol=https \
           file://0001-Make-xmlError-structs-constant.patch \
           "
SRCREV = "85a1788c6977419b6facad11dbfbf823e739eb3b"

S = "${WORKDIR}/git"

inherit meson pkgconfig

BBCLASSEXTEND = "native"
