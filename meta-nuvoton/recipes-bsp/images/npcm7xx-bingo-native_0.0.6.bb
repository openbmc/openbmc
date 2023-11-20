SUMMARY = "XML-based binary image generator"
DESCRIPTION = "XML-based binary image generator"
HOMEPAGE = "https://github.com/Nuvoton-Israel/bingo"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Nuvoton-Israel/bingo;branch=master;protocol=https"

# tag Bingo_0.0.6
SRCREV = "1692a12d38f1c62ef7d870a8d2687e70888e1779"

S = "${WORKDIR}/git"

do_install () {

	install -d "${D}${bindir}"
	install deliverables/linux/Release/bingo ${D}${bindir}
}

inherit native
