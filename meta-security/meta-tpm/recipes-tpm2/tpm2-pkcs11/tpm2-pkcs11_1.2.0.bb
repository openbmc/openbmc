SUMMARY = "A PKCS#11 interface for TPM2 hardware"
DESCRIPTION = "PKCS #11 is a Public-Key Cryptography Standard that defines a standard method to access cryptographic services from tokens/ devices such as hardware security modules (HSM), smart cards, etc. In this project we intend to use a TPM2 device as the cryptographic token."
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fc19f620a102768d6dbd1e7166e78ab"

DEPENDS = "autoconf-archive pkgconfig dstat sqlite3 openssl libtss2-dev tpm2-tools libyaml"

SRC_URI = "git://github.com/tpm2-software/tpm2-pkcs11.git;branch=1.X \
           file://bootstrap_fixup.patch "

SRCREV = "8d8f137f65f1d61d66cc191947b59c378f23e97d"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

do_configure_prepend () {
    ${S}/bootstrap
}
