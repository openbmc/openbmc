SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4ui libwnck thunar garcon exo"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "5db09df39b0808f25bd3a3128f8c9e77"
SRC_URI[sha256sum] = "a30534461fea907f969f608a11c84be0b1aaad687c591c32cd56a9d274ea3e74"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES_${PN} += "${datadir}/backgrounds"
