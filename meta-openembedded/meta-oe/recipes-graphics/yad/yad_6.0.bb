SUMMARY = "Yet Another Dialog"
DESCRIPTION = "Program allowing you to display GTK+ dialog boxes from command line or shell scripts."
AUTHOR = "Victor Ananjevsky"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://github.com/v1cont/yad.git;branch=master;protocol=https"
SRCREV = "a5b1a7a3867bc7dffbbc539f586f301687b6ec02"

inherit autotools gsettings features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "gtk+3 glib-2.0-native intltool-native"

S = "${WORKDIR}/git"

FILES_${PN} += "${datadir}/icons/"
