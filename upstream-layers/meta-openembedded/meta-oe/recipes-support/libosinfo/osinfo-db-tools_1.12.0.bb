SUMMARY = "Tools for managing the libosinfo database files"
HOMEPAGE = "https://libosinfo.org"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "glib-2.0 json-glib libarchive libsoup"

SRC_URI = "git://gitlab.com/libosinfo/osinfo-db-tools.git;branch=main;protocol=https \
           "
SRCREV = "e5564be303bfac49cc3490bd0fada342cd65566f"


inherit meson pkgconfig

do_configure:prepend () {
    sed -i 's/-Werror//' ${S}/meson.build
}

BBCLASSEXTEND = "native"
