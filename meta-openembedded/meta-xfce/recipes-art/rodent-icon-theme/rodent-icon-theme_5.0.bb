SUMMARY = "Rodent-icon-theme (was xfce4-icon-theme) is a svg icon theme"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

inherit allarch autotools gtk-icon-cache

SRC_URI = "http://sourceforge.net/projects/xffm/files/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "4b097d344a160d6497c6498985c8df15"
SRC_URI[sha256sum] = "6ed00d16faa1f55b3bb7b13862de1d7f5cfd978b93e42487ded21595d0dbe208"

FILES_${PN} += "${datadir}/icons"

RREPLACES_${PN} += "xfce4-icon-theme"
RPROVIDES_${PN} += "xfce4-icon-theme"
RCONFLICTS_${PN} += "xfce4-icon-theme"

