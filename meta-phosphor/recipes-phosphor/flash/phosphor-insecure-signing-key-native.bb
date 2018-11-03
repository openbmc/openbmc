SUMMARY = "Insecure private key for testing and development"
DESCRIPTION = "Do not use this key to sign images."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit native

SRC_URI += "file://OpenBMC.priv"

do_install() {
	bbplain "Using an insecure image signing key!"
	install -d ${D}${datadir}
	install -m 400 ${WORKDIR}/OpenBMC.priv ${D}${datadir}
}
