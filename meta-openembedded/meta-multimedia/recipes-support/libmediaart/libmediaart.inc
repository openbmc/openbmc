SUMMARY = "Library tasked with managing, extracting and handling media art caches"

LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
                    file://libmediaart/extract.c;endline=18;md5=dff2b6328ab067b5baadc135f9876c36 \
                    file://tests/mediaarttest.c;endline=18;md5=067106eaa1f7a9d918759a096667f18e"

DEPENDS = "glib-2.0 gdk-pixbuf"

inherit gnomebase gtk-doc gobject-introspection

EXTRA_OECONF = "--disable-qt --enable-gdkpixbuf"
