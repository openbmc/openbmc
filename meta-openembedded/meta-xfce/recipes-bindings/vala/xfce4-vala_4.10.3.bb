SUMMARY = "Xfce4 Vala provides bindings for the Xfce framework"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"
DEPENDS = "libxfce4util garcon xfconf libxfce4ui xfce4-panel exo vala xfce4-dev-tools-native"

inherit xfce pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = " \
    http://archive.xfce.org/src/bindings/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.bz2 \
    file://0001-configure.ac-Detect-vapidir-if-not-set-explicitly.patch \
"
SRC_URI[md5sum] = "0bbb1d6e473e0fe9b335b7b1b49d8a71"
SRC_URI[sha256sum] = "07a8f2b7c09fcdd3d86e0c52adea3c58ca011d0142a93997a01b4af77260ae7b"

FILES_${PN} += "${datadir}/vala-*/vapi"

RDEPENDS_${PN} = "vala"
