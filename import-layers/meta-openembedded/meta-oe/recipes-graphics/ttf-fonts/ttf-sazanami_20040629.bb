require ttf.inc

SUMMARY = "Sazanami Gothic/Mincho Japanese TrueType fonts"
SUMMARY_ttf-sazanami-gothic = "Sazanami Gothic Japanese TrueType font"
SUMMARY_ttf-sazanami-mincho = "Sazanami Mincho Japanese TrueType font"
AUTHOR = "Electronic Font Open Laboratory (/efont/)"
HOMEPAGE = "http://sourceforge.jp/projects/efont/"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://README;md5=97d739900be6e852830f55aa3c07d4a0"
SRC_DISTRIBUTE_LICENSES += "${PN}"
RPROVIDES_${PN} = "virtual-japanese-font"
PR = "r9"

SRC_URI = "http://osdn.dl.sourceforge.jp/efont/10087/sazanami-20040629.tar.bz2"
S = "${WORKDIR}/sazanami-20040629"

PACKAGES = "ttf-sazanami-gothic ttf-sazanami-mincho"
FONT_PACKAGES = "ttf-sazanami-gothic ttf-sazanami-mincho"

FILES_ttf-sazanami-gothic = "${datadir}/fonts/truetype/sazanami-gothic.ttf \
                             ${datadir}/doc/ttf-sazanami-gothic/README"
FILES_ttf-sazanami-mincho = "${datadir}/fonts/truetype/sazanami-mincho.ttf \
                             ${datadir}/doc/ttf-sazanami-mincho/README"

do_install_append() {
    # README contains the redistribution license
    install -d ${D}${datadir}/doc/
    install -d ${D}${datadir}/doc/ttf-sazanami-gothic
    install -d ${D}${datadir}/doc/ttf-sazanami-mincho
    install -m 0644 ${S}/README ${D}${datadir}/doc/ttf-sazanami-gothic
    install -m 0644 ${S}/README ${D}${datadir}/doc/ttf-sazanami-mincho
}

SRC_URI[md5sum] = "ceef10579a75c92483171f3bd7f77df2"
SRC_URI[sha256sum] = "3467ce2f70a9a3fbbf8d4d97355a2f334a6351baa6722251403637a8cbebf6b7"
