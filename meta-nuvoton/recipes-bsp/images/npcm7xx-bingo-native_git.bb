SUMMARY = "XML-based binary image generator"
DESCRIPTION = "XML-based binary image generator"
HOMEPAGE = "https://github.com/Nuvoton-Israel/bingo"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += "git://github.com/Nuvoton-Israel/bingo"
SRCREV = "4f102ff7851da9fd11965857edd1b3046c187b7a"

S = "${WORKDIR}/git"

do_install () {

	install -d "${D}${bindir}"
	install deliverables/linux/Release/bingo ${D}${bindir}
}

inherit native
