SUMMARY = "GNOME keyboard library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6e29c688d912da12b66b73e32b03d812"

SECTION = "x11/gnome/libs"

DEPENDS = "gconf gtk+ glib-2.0 libxklavier"

inherit gnome

SRC_URI[archive.md5sum] = "de32a6e3e3464b566eecdc4332bf34bd"
SRC_URI[archive.sha256sum] = "ddd52c4cc7d83ad7ef964a1bcb4db87407e65b00ffc3e70c088ca4ee7383d256"
GNOME_COMPRESS_TYPE="bz2"

do_configure_append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
    find ${B} -name Makefile | xargs sed -i s:'-I/usr/include':'-I${STAGING_INCDIR}':g
}

