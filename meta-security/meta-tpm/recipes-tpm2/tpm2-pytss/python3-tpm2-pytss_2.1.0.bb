DESCRIPTION = "TPM2 TSS Python bindings for Enhanced System API (ESYS), Feature API (FAPI), Marshaling (MU), TCTI Loader (TCTILdr), TCTIs, policy, and RC Decoding (rcdecode) libraries"
HOMEPAGE = "https://github.com/tpm2-software/tpm2-pytss"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

SRC_URI[sha256sum] = "5b5b4b1456fdc1aeef3d2c3970beaa078c8f7f2648c97a69bcf60c5a2f95c897"

PYPI_PACKAGE = "tpm2-pytss"

DEPENDS = "python3-pkgconfig-native python3-pycparser-native python3-asn1crypto-native"
DEPENDS:append = " python3-cryptography-native tpm2-tss" 

inherit autotools pkgconfig pypi setuptools3_legacy

RDEPENDS:${PN} = "libtss2"
