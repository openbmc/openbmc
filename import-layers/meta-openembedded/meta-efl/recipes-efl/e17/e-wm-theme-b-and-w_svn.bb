DESCRIPTION = "b-and-w theme was default E17 theme before alpha3 and some people still prefers it."
SECTION = "e/utils"
DEPENDS = "edje-native"
RDEPENDS_${PN} = "e-wm"
LICENSE = "MIT & BSD"
# upstream was asked to include license infor in THEME dir
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

SRCREV = "${EFL_SRCREV}"
PV = "0.0+svnr${SRCPV}"

inherit e-base

SRCNAME = "b_and_w"
SRC_URI = "${E_SVN}/trunk/THEMES;module=${SRCNAME};protocol=http"
S = "${WORKDIR}/${SRCNAME}/e"

do_compile() {
    # unfortunately hardcoded edje_cc in Makefile     
    sed -i "s#\tedje_cc#\t${STAGING_BINDIR_NATIVE}/edje_cc#g" Makefile
    make
}

do_install() {
    install -d ${D}${datadir}/enlightenment/data/themes/
    install -m 0644 ${S}/b_and_w.edj ${D}${datadir}/enlightenment/data/themes/
}

FILES_${PN} = "${datadir}/enlightenment/data/themes/"
