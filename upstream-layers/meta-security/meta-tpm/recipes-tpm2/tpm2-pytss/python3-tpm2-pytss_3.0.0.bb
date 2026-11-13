SUMMARY = "Python bindings for the TPM2 Software Stack (TSS2)"
DESCRIPTION = "TPM2 TSS Python bindings for Enhanced System API (ESYS), Feature API (FAPI), Marshaling (MU), TCTI Loader (TCTILdr), TCTIs, policy, and RC Decoding (rcdecode) libraries"
HOMEPAGE = "https://github.com/tpm2-software/tpm2-pytss"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

PYPI_PACKAGE_SDIST = "tpm2_pytss"

SRC_URI += "file://0001-Handle-CC-values-with-arguments.patch"
SRC_URI[sha256sum] = "c193cf1908b482f93c4aa464132920fe3a37d4b9d802e267974ff2dfe37d4433"

inherit autotools pkgconfig pypi python_setuptools_build_meta

DEPENDS = " \
    python3-setuptools-scm-native \
    python3-asn1crypto-native \
    python3-cryptography-native \
    python3-pkgconfig-native \
    python3-pycparser-native \
    tpm2-tss \
"

RDEPENDS:${PN} = "libtss2 python3-asn1crypto"

BBCLASSEXTEND = "native nativesdk"
