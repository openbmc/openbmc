require ttf.inc

SUMMARY = "The project goal is to improve existing offerings of the fonts \
- making sure the Lohit fonts deliver the best available quality and functions \
to the community."
HOMEPAGE = "https://fedorahosted.org/lohit"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=7dfa0a236dc535ad2d2548e6170c4402"

SRCREV = "d678f1b1807ea5602586279e90b5db6d62ed475e"
SRC_URI = "git://github.com/pravins/lohit.git;branch=master;protocol=https"

DEPENDS = "fontforge-native"
S = "${WORKDIR}/git"
FONT_PACKAGES = "${PN}"
FILES_${PN} = "${datadir}"

inherit python3native

do_compile() {
    cd ${S}; make ttf;
}

