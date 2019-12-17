SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0eb1216e46938bd723098d93a23c3bcc"
SECTION = "tpm"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "071aa40bc8721700ea4ed19cc2fdeabf"
SRC_URI[sha256sum] = "ccec3fca6370341a102c5c2ef1ddb4e5cd242bf1bbc6c51d969f77fc78ca67d1"

inherit autotools pkgconfig bash-completion
