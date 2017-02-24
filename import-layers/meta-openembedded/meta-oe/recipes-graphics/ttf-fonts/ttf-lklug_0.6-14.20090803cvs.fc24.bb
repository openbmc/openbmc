require ttf.inc

SUMMARY = "Fonts for Sinhala language - TTF Edition"
HOMEPAGE = "http://rpms.famillecollet.com/rpmphp/zoom.php?rpm=lklug-fonts"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://dl.fedoraproject.org/pub/fedora/linux/releases/24/Everything/source/tree/Packages/l/lklug-fonts-0.6-14.20090803cvs.fc24.src.rpm;extract=lklug-20090803.tar.gz"
SRC_URI[md5sum] = "3341dfb997043d234ab3f6e5a965e759"
SRC_URI[sha256sum] = "f54c1f3b4df08995982657fed290b562556191fee2a0386afd9e8bf228f72b1a"

DEPENDS = "fontforge-native"

S = "${WORKDIR}"
FONT_PACKAGES = "${PN}"
FILES_${PN} = "${datadir}"

do_compile() {
    fontforge ${S}/convert.ff lklug
}

do_install() {
    make install DESTDIR=${D}
}
