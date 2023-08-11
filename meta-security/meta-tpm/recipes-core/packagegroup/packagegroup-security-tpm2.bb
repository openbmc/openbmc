DESCRIPTION = "TPM2 packagegroup for Security"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

PACKAGES = "${PN}"

SUMMARY:packagegroup-security-tpm2 = "Security TPM 2.0 support"
RDEPENDS:packagegroup-security-tpm2 = " \
    tpm2-tools \
    trousers \
    tpm2-tss \
    libtss2-mu \
    libtss2-tcti-device \
    libtss2-tcti-mssim \
    libtss2 \
    tpm2-abrmd \
    tpm2-pkcs11 \
    tpm2-openssl \
    tpm2-tss-engine \
    tpm2-tss-engine-engines \
    python3-tpm2-pytss \
    "
