SUMMARY = "Display dialog boxes from the commandline and shell scripts"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

PR = "r2"

PNBLACKLIST[zenity] ?= "BROKEN: doesn't build with B!=S"

DEPENDS = "gtk+ glib-2.0 libnotify gnome-doc-utils"

inherit gnomebase
SRC_URI += "file://0001-Makefile.am-don-t-build-help.patch"
SRC_URI[archive.md5sum] = "aa66ec35451b16e424519b4973082170"
SRC_URI[archive.sha256sum] = "8838be041a07364b62a4281c971392e4a09bb01bb3237a836ec0457ec0ea18ac"

EXTRA_OECONF += "--disable-scrollkeeper"
# remove -I/usr/include from zenity_CPPFLAGS
do_configure_prepend() {
    sed -i -e '/-I$(includedir)/d' src/Makefile.am
}

