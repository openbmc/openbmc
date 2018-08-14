SUMMARY = "Modified version of the xfce-dusk theme to support also gtk 3.x too"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f0e2cd40e05189ec81232da84bd6e1a"

inherit allarch

SRC_URI = "http://xfce-look.org/CONTENT/content-files/141404-xfce_dusk_gtk3-1_3.tar.gz;subdir=${BPN}-${PV}"
SRC_URI[md5sum] = "b3ad37ad8173b14ec090e60a80e65d8f"
SRC_URI[sha256sum] = "bfa8a88607d1a1da5bd0b9e4e075767c54400a3c5a0fae88b619ed71532f30b4"

do_configure() {
}

do_install() {
    install -d ${D}${datadir}/themes
    cp -r Xfce-dusk-gtk3 ${D}${datadir}/themes/Xfce-dusk-gtk3
}

FILES_${PN} = "${datadir}/themes"
