SUMMARY = "Common macros for building GNOME applications"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "x11/gnome"
inherit gnomebase allarch

SRC_URI[archive.md5sum] = "933258d9c23e218eb6eec9cc1951b053"
SRC_URI[archive.sha256sum] = "22569e370ae755e04527b76328befc4c73b62bfd4a572499fde116b8318af8cf"

EXTRA_AUTORECONF = ""
DEPENDS = ""

# Default to enable autoconf-archive to avoid conflicts
PACKAGECONFIG ??= "autoconf-archive"
PACKAGECONFIG[autoconf-archive] = "--with-autoconf-archive, --without-autoconf-archive, autoconf-archive"

FILES_${PN} += "${datadir}/aclocal"
FILES_${PN}-dev = ""

BBCLASSEXTEND = "native"
