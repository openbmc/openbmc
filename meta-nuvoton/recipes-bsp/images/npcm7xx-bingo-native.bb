LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += "git://github.com/Nuvoton-Israel/bingo"
SRC_URI += "file://BootBlockAndHeader_EB.xml"
SRC_URI += "file://UbootHeader_EB.xml"
SRC_URI += "file://mergedBootBlockAndUboot.xml"

SRCREV = "4f102ff7851da9fd11965857edd1b3046c187b7a"

S = "${WORKDIR}/git"

do_install () {

	install -d "${D}${bindir}"
	install deliverables/linux/Release/bingo ${D}${bindir}
	install ${WORKDIR}/*.xml ${D}${bindir}
}

inherit native
