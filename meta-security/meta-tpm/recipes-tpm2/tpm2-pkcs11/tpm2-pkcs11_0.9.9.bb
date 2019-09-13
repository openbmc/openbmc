SUMMARY = "A PKCS#11 interface for TPM2 hardware"
DESCRIPTION = "PKCS #11 is a Public-Key Cryptography Standard that defines a standard method to access cryptographic services from tokens/ devices such as hardware security modules (HSM), smart cards, etc. In this project we intend to use a TPM2 device as the cryptographic token."
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=93645981214b60a02688745c14f93c95"

DEPENDS = "autoconf-archive pkgconfig dstat sqlite3 openssl libtss2-dev tpm2-tools"

SRC_URI = "git://github.com/tpm2-software/tpm2-pkcs11.git \
           file://bootstrap_fixup.patch \
          "

SRCREV = "caf20c04651029626466c59d88b36c05cc6ea20b"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

do_configure_prepend () {
    ${S}/bootstrap
}
