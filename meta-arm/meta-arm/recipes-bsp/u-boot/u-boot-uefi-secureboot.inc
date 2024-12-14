FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://uefi-secureboot.cfg"

inherit sbsign

require ${@bb.utils.contains('MACHINE_FEATURES', 'uefi-http-boot', 'u-boot-uefi-http-boot.inc', '', d)}
require ${@bb.utils.contains('MACHINE_FEATURES', 'uefi-capsule-update', 'u-boot-capsule-update.inc', '', d)}

DEPENDS += 'python3-pyopenssl-native'

do_compile:prepend() {
    export CRYPTOGRAPHY_OPENSSL_NO_LEGACY=1

    "${S}"/tools/efivar.py set -i "${S}"/ubootefi.var -n pk  -d "${SBSIGN_KEYS_DIR}"/PK.esl  -t file
    "${S}"/tools/efivar.py set -i "${S}"/ubootefi.var -n kek -d "${SBSIGN_KEYS_DIR}"/KEK.esl -t file
    "${S}"/tools/efivar.py set -i "${S}"/ubootefi.var -n db  -d "${SBSIGN_KEYS_DIR}"/db.esl  -t file
    "${S}"/tools/efivar.py set -i "${S}"/ubootefi.var -n dbx -d "${SBSIGN_KEYS_DIR}"/dbx.esl -t file
    "${S}"/tools/efivar.py print -i "${S}"/ubootefi.var
}
