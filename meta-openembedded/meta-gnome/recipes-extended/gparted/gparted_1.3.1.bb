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
SRC_URI[sha256sum] = "5eee2e6d74b15ef96b13b3a2310c868ed2298e03341021e7d12a5a98a1d1e109"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/gparted/files/gparted/"
UPSTREAM_CHECK_REGEX = "gparted-(?P<pver>\d+\.(\d+)+(\.\d+)+)"

DEPENDS += " \
    glib-2.0-native \
    yelp-tools-native \
    intltool-native \
    glib-2.0 \
    gtkmm3 \
    parted \
"

FILES:${PN} += " \
    ${datadir}/appdata \
    ${datadir}/icons \
"

PACKAGES += "${PN}-polkit"
FILES:${PN}-polkit = "${datadir}/polkit-1"

RDEPENDS:${PN} = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', '${PN}-polkit', '', d)} \
    dosfstools \
    mtools \
    e2fsprogs \
"
