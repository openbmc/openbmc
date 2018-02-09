SUMMARY = "Insecure private key for testing and development"
DESCRIPTION = "Do not use this key to sign images."
PR = "r1"

inherit allarch
inherit native
inherit obmc-phosphor-license

SRC_URI += "file://OpenBMC.priv"

do_install() {
	bbplain "Using an insecure image signing key!"
	install -d ${D}${datadir}
	install -m 400 ${WORKDIR}/OpenBMC.priv ${D}${datadir}
}
