require gtk+3.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk+/${MAJ_VER}/gtk+-${PV}.tar.xz \
           file://hardcoded_libtool.patch \
           file://Dont-force-csd.patch \
           file://Do-not-try-to-initialize-GL-without-libGL.patch \
          "

SRC_URI[md5sum] = "fc59e5c8b5a4585b60623dd708df400b"
SRC_URI[sha256sum] = "4d12726d0856a968b41802ae5c5971d7e9bac532717e309d3f81b9989da5ffbe"

S = "${WORKDIR}/gtk+-${PV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
                    file://gtk/gtk.h;endline=25;md5=1d8dc0fccdbfa26287a271dce88af737 \
                    file://gdk/gdk.h;endline=25;md5=c920ce39dc88c6f06d3e7c50e08086f2 \
                    file://tests/testgtk.c;endline=25;md5=cb732daee1d82af7a2bf953cf3cf26f1"
