SUMMARY = "A partition editor to graphically manage disk partitions "
HOMEPAGE = "http://gparted.org/index.php"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit features_check autotools pkgconfig python3native gnome-help gtk-icon-cache

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.gz \
    file://0001-Install-polkit-action-unconditionally-executable-pke.patch \
"
SRC_URI[sha256sum] = "8dc180245dd9ea45e6e2f4bc69512f187e08be7f799c98a825a0b04c161cbd2a"

DEPENDS += " \
    glib-2.0-native \
    yelp-tools-native \
    intltool-native \
    glib-2.0 \
    gtkmm3 \
    parted \
"

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/icons \
"

PACKAGES += "${PN}-polkit"
FILES_${PN}-polkit = "${datadir}/polkit-1"

RDEPENDS_${PN} = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', '${PN}-polkit', '', d)} \
    dosfstools \
    mtools \
    e2fsprogs \
"
