SUMMARY = "XML-based binary image generator"
DESCRIPTION = "XML-based binary image generator"
HOMEPAGE = "https://github.com/Nuvoton-Israel/bingo"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += "git://github.com/Nuvoton-Israel/bingo;branch=master;protocol=https"
SRCREV = "7c35658b667d04d6cc78b7ed569f4401168ae133"

S = "${WORKDIR}/git"

do_install () {

	install -d "${D}${bindir}"
	install deliverables/linux/Release/bingo ${D}${bindir}
}

inherit native
