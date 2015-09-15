require grub2.inc

RDEPENDS_${PN} = "diffutils freetype"
PR = "r1"

FILES_${PN}-dbg += "${libdir}/${BPN}/i386-pc/.debug"


EXTRA_OECONF = "--with-platform=pc --disable-grub-mkfont --program-prefix="" \
               --enable-liblzma=no --enable-device-mapper=no --enable-libzfs=no"

do_install_append () {
    install -d ${D}${sysconfdir}/grub.d
}

INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"
