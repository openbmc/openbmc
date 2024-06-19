require ttf.inc

SUMMARY = "Fonts for Sinhala language - TTF Edition"
HOMEPAGE = "http://rpms.famillecollet.com/rpmphp/zoom.php?rpm=lklug-fonts"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://src.fedoraproject.org/repo/pkgs/lklug-fonts/lklug-20090803.tar.gz/b6e0daaf8cf41208fd2a7bc04fb23f84/lklug-20090803.tar.gz"
SRC_URI[sha256sum] = "de5750f7048973f851961050f76b8b58e9bda400d5007c3078d9317fbe2ff5fd"

DEPENDS = "fontforge-native"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
FONT_PACKAGES = "${PN}"
FILES:${PN} = "${datadir}"

do_compile() {
    fontforge ${S}/convert.ff lklug
}

do_install() {
    make install DESTDIR=${D}
}
