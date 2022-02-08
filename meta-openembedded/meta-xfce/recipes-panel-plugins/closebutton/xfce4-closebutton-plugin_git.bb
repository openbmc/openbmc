SUMMARY = "Plugin displaying close button for application currently active"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

inherit xfce-panel-plugin xfce-git gtk-icon-cache perlnative

DEPENDS += "exo-native libwnck3 xfconf"

PV = "0.1.0+gitr${SRCPV}"

SRC_URI = "git://github.com/schnitzeltony/xfce4-closebutton-plugin.git;branch=master;protocol=https"
SRCREV = "6ed5c3ee1ba7103ca854c5e81fb2c1220b913a40"
S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-maintainer-mode"

FILES_${PN} += "${datadir}/xfce4/closebutton/themes"
