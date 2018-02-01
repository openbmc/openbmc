SECTION = "x11/gnome/libs"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34"

inherit gnomebase pkgconfig gtk-doc

SRC_URI += "file://gcc5.patch \
            file://0001-bonobo-ui-node-qualify-functions-with-G_GNUC_PRINTF.patch \
            "

SRC_URI[archive.md5sum] = "853be8e28aaa4ce48ba60be7d9046bf4"
SRC_URI[archive.sha256sum] = "fab5f2ac6c842d949861c07cb520afe5bee3dce55805151ce9cd01be0ec46fcd"
GNOME_COMPRESS_TYPE="bz2"

DEPENDS = "libgnomecanvas libbonobo libgnome glib-2.0 gconf libxml2 libglade gnome-common intltool-native"

FILES_${PN} += "${libdir}/libglade/2.0/*.so"
FILES_${PN}-dev += "${libdir}/libglade/2.0/*.la ${datadir}/gnome-2.0/ui \
                    ${libdir}/bonobo-2.0/samples"
FILES_${PN}-staticdev += "${libdir}/libglade/2.0/*.a"
FILES_${PN}-dbg += "${libdir}/bonobo-2.0/samples/.debug \
                    ${libdir}/libglade/2.0/.debug"
