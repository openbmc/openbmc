SUMMARY = "Plugin displaying close button for application currently active"
SECTION = "x11"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b6952d9a47fc2ad0f315510e1290455f"

inherit xfce-panel-plugin xfce-git gtk-icon-cache perlnative

DEPENDS += "xfce4-dev-tools-native libwnck3 xfconf"

PV = "4.16.0"

SRC_URI = "git://github.com/schnitzeltony/xfce4-closebutton-plugin.git;branch=master;protocol=https"
SRCREV = "538f9acfc5d5019f5cde734d056bcc0c95da9b4c"
S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-maintainer-mode"

FILES:${PN} += "${datadir}/xfce4/closebutton/themes"
