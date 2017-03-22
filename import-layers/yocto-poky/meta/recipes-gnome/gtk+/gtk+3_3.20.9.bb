require gtk+3.inc

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk+/${MAJ_VER}/gtk+-${PV}.tar.xz \
           file://0001-Hardcoded-libtool.patch \
           file://0002-Do-not-try-to-initialize-GL-without-libGL.patch \
           file://0003-Add-disable-opengl-configure-option.patch \
           file://0004-configure.ac-Fix-wayland-protocols-path.patch \
           file://0001-Redo-focus-handling-in-treeview-once-more.patch \
          "
SRC_URI[md5sum] = "cc76cac5e18c772c6784bf19a3dff08b"
SRC_URI[sha256sum] = "83a609ba2f3424b5509e73967c49c67833af466d6f91081b24ee5c64fce6ac17"

S = "${WORKDIR}/gtk+-${PV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
                    file://gtk/gtk.h;endline=25;md5=1d8dc0fccdbfa26287a271dce88af737 \
                    file://gdk/gdk.h;endline=25;md5=c920ce39dc88c6f06d3e7c50e08086f2 \
                    file://tests/testgtk.c;endline=25;md5=cb732daee1d82af7a2bf953cf3cf26f1"
