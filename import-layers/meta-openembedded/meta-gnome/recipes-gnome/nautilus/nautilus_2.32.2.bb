# nautilus OE build file
# Copyright (C) 2005, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

LICENSE="GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=7c0048536e43642a1f3a724c2909872b \
                    file://COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a"

PR = "r6"

DEPENDS = "gdk-pixbuf gtk+ libunique gvfs librsvg libexif esound gnome-desktop orbit2-native"
# optional: tracker

inherit gnome gobject-introspection

SRC_URI[archive.md5sum] = "51565aa10d1625dff56e381228346911"
SRC_URI[archive.sha256sum] = "2d4ff28c7a7aa5d40eb2468149954a564c257a305183773057584d22d15347a2"
GNOME_COMPRESS_TYPE="bz2"

SRC_URI += "file://idl-sysroot.patch \
            file://no-try-run-strftime.diff \
            file://no-G_DISABLE_DEPRECATED.patch \
"


EXTRA_OECONF = " --disable-gtk-doc  --disable-update-mimedb "
export SYSROOT = "${STAGING_DIR_HOST}"

do_configure() {
    sed -i -e /docs/d ${S}/Makefile.am
    autotools_do_configure
    # We need native orbit-idl with target idl files. No way to say it in a clean way:
    find ${B} -name Makefile -exec sed -i '/\/usr\/bin\/orbit-idl-2/{s:/usr/bin:${STAGING_BINDIR_NATIVE}:;s:/usr/share:${STAGING_DATADIR}:g}' {} \;
}

RDEPENDS_${PN} = "gvfs gvfsd-ftp gvfsd-sftp gvfsd-trash glib-networking"
FILES_${PN} += "${datadir}/icons"

# Don't make nautils3 drag us in
PRIVATE_LIBS = "libnautilus-extension.so.1"

