DESCRIPTION = "TPM2 TSS Python bindings for Enhanced System API (ESYS), Feature API (FAPI), Marshaling (MU), TCTI Loader (TCTILdr), TCTIs, policy, and RC Decoding (rcdecode) libraries"
HOMEPAGE = "https://github.com/tpm2-software/tpm2-pytss"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

PYPI_PACKAGE = "tpm2-pytss"

SRC_URI[sha256sum] = "20071129379656f5f3c3bc16d364612672b147d81191fb4eb9f9ff9fbee48410"

inherit autotools pkgconfig pypi python_setuptools_build_meta

DEPENDS = " \
    python3-setuptools-scm-native \
    python3-asn1crypto-native \
    python3-cryptography-native \
    python3-pkgconfig-native \
    python3-pycparser-native \
    tpm2-tss \
"

RDEPENDS:${PN} = "libtss2"
