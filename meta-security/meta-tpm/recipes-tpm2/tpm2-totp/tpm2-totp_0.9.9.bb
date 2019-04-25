SUMMARY = "The tpm2-tss-engine project implements a cryptographic engine for OpenSSL." 
DESCRIPTION = "The tpm2-tss-engine project implements a cryptographic engine for OpenSSL for Trusted Platform Module (TPM 2.0) using the tpm2-tss software stack that follows the Trusted Computing Groups (TCG) TPM Software Stack (TSS 2.0). It uses the Enhanced System API (ESAPI) interface of the TSS 2.0 for downwards communication. It supports RSA decryption and signatures as well as ECDSA signatures."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed23833e93c95173c8d8913745e4b4e1"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive libtss2-dev qrencode"

SRCREV = "44fcb6819f79302d5a088b3def648616e3551d4a"
SRC_URI = "git://github.com/tpm2-software/tpm2-totp.git \
           file://litpm2_totp_build_fix.patch "

inherit autotools-brokensep pkgconfig

S = "${WORKDIR}/git"
