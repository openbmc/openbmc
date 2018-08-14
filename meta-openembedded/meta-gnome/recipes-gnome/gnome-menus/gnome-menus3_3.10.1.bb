SUMMARY = "GNOME menus"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "python libxml2 gconf popt gtk+3 intltool-native gnome-common-native"

inherit distro_features_check gnomebase pkgconfig python-dir pythonnative gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

BPN = "gnome-menus"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"
SRC_URI = "${GNOME_MIRROR}/${BPN}/${SHRT_VER}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "6db025e79e2b69f39fc7aa0753f43081"
SRC_URI[sha256sum] = "46950aba274c1ad58234374fa9b235258650737307f3bc396af48eb983668a71"

FILES_${PN} += "${datadir}/desktop-directories/"
