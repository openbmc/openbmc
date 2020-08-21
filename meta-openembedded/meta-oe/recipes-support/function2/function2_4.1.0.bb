SUMMARY = "Improved drop-in replacement for std::function"
DESCRIPTION = "Provides improved implementations of std::function."
HOMEPAGE = "https://naios.github.io/function2"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"
SRCREV = "3a0746bf5f601dfed05330aefcb6854354fce07d"
PV .= "+git${SRCPV}"

SRC_URI += "gitsm://github.com/Naios/function2"

S = "${WORKDIR}/git"

inherit cmake
inherit ptest

# Installs some data to incorrect top-level /usr directory
do_install_append() {
	mkdir -p ${D}/${datadir}/function2
	mv ${D}/${prefix}/Readme.md ${D}/${datadir}/function2/
	mv ${D}/${prefix}/LICENSE.txt ${D}/${datadir}/function2/
}
