SUMMARY = "OpenBMC image signing public key"
DESCRIPTION = "Public key information to be included in images for image verification."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch

INSECURE_KEY = "${@'${SIGNING_KEY}' == '${STAGING_DIR_NATIVE}${datadir}/OpenBMC.priv'}"

DEPENDS += "openssl-native"
DEPENDS += "${@oe.utils.conditional('INSECURE_KEY', 'True', 'phosphor-insecure-signing-key-native', '', d)}"

FILES_${PN} += "${sysconfdir}/activationdata/"

SIGNING_KEY ?= "${STAGING_DIR_NATIVE}${datadir}/OpenBMC.priv"
SIGNING_KEY_TYPE = "${@os.path.splitext(os.path.basename('${SIGNING_KEY}'))[0]}"

do_install() {
	openssl pkey -in "${SIGNING_KEY}" -pubout -out ${WORKDIR}/publickey
	echo HashType=RSA-SHA256 > "${WORKDIR}/hashfunc"

	idir="${D}${sysconfdir}/activationdata/${SIGNING_KEY_TYPE}"

	install -d ${idir}
	install -m 644 ${WORKDIR}/publickey ${idir}
	install -m 644 ${WORKDIR}/hashfunc ${idir}
}

SYSROOT_DIRS_append = " ${sysconfdir}"
