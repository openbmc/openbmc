SUMMARY = "Yet Another Dialog"
DESCRIPTION = "Program allowing you to display GTK+ dialog boxes from command line or shell scripts."
AUTHOR = "Victor Ananjevsky"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://github.com/v1cont/yad.git"
SRCREV = "3857a0a8822fc0a7206e804f15cc17b85a5f8ce2"

inherit autotools gsettings

DEPENDS = "gtk+3 glib-2.0-native intltool-native"

S = "${WORKDIR}/git"

FILES_${PN} += "${datadir}/icons/"
