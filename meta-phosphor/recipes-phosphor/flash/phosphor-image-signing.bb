SUMMARY = "OpenBMC image signing public key"
DESCRIPTION = "Public key information to be included in images for image verification."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += "openssl-native"
DEPENDS += "${@oe.utils.conditional('INSECURE_KEY', 'True', 'phosphor-insecure-signing-key-native', '', d)}"
PR = "r1"

SIGNING_PUBLIC_KEY ?= ""
SIGNING_PUBLIC_KEY_TYPE = "${@os.path.splitext(os.path.basename('${SIGNING_PUBLIC_KEY}'))[0]}"
SIGNING_KEY ?= "${STAGING_DIR_NATIVE}${datadir}/OpenBMC.priv"
SIGNING_KEY_TYPE = "${@os.path.splitext(os.path.basename('${SIGNING_KEY}'))[0]}"
SYSROOT_DIRS:append = " ${sysconfdir}"

inherit allarch

do_install() {
        signing_key="${SIGNING_KEY}"
        if [ "${INSECURE_KEY}" = "True" ] && [ -n "${SIGNING_PUBLIC_KEY}" ]; then
            echo "Using SIGNING_PUBLIC_KEY"
            signing_key=""
        fi
        if [ -n "${signing_key}" ] && [ -n "${SIGNING_PUBLIC_KEY}" ]; then
            echo "Both SIGNING_KEY and SIGNING_PUBLIC_KEY are defined, expecting only one"
            exit 1
        fi
        if [ -n "${signing_key}" ]; then
            openssl pkey -in "${signing_key}" -pubout -out ${UNPACKDIR}/publickey
            idir="${D}${sysconfdir}/activationdata/${SIGNING_KEY_TYPE}"
        elif [ -n "${SIGNING_PUBLIC_KEY}" ]; then
            cp "${SIGNING_PUBLIC_KEY}" ${UNPACKDIR}/publickey
            idir="${D}${sysconfdir}/activationdata/${SIGNING_PUBLIC_KEY_TYPE}"
        else
            echo "No SIGNING_KEY or SIGNING_PUBLIC_KEY defined, expecting one"
            exit 1
        fi
        echo HashType=RSA-SHA256 > "${UNPACKDIR}/hashfunc"
        install -d ${idir}
        install -m 644 ${UNPACKDIR}/publickey ${idir}
        install -m 644 ${UNPACKDIR}/hashfunc ${idir}
}

FILES:${PN} += "${sysconfdir}/activationdata/"

INSECURE_KEY = "${@'${SIGNING_KEY}' == '${STAGING_DIR_NATIVE}${datadir}/OpenBMC.priv'}"
