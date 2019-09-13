SUMMARY = "Insecure private key for testing and development"
DESCRIPTION = "Do not use this key to sign images."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit native

SRC_URI += "file://OpenBMC.priv"

do_install() {
	bbplain "Using an insecure image signing key!"
	install -d ${D}${datadir}
	install -m 400 ${WORKDIR}/OpenBMC.priv ${D}${datadir}
}
