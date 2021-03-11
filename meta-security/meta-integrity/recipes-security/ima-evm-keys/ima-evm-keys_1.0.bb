SUMMARY = "IMA/EMV public keys"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit features_check
REQUIRED_DISTRO_FEATURES = "ima"

ALLOW_EMPTY_${PN} = "1"

do_install () {
    if [ -e "${IMA_EVM_X509}" ]; then
        install -d ${D}/${sysconfdir}/keys
        install "${IMA_EVM_X509}" ${D}${sysconfdir}/keys/x509_evm.der
        lnr ${D}${sysconfdir}/keys/x509_evm.der ${D}${sysconfdir}/keys/x509_ima.der
    fi
}
