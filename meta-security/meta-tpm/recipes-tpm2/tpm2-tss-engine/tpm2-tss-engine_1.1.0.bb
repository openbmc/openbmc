SUMMARY = "The tpm2-tss-engine project implements a cryptographic engine for OpenSSL." 
DESCRIPTION = "The tpm2-tss-engine project implements a cryptographic engine for OpenSSL for Trusted Platform Module (TPM 2.0) using the tpm2-tss software stack that follows the Trusted Computing Groups (TCG) TPM Software Stack (TSS 2.0). It uses the Enhanced System API (ESAPI) interface of the TSS 2.0 for downwards communication. It supports RSA decryption and signatures as well as ECDSA signatures."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b3ab643b9ce041de515d1ed092a36d4"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive-native bash-completion libtss2 libgcrypt openssl"

SRCREV = "6f387a4efe2049f1b4833e8f621c77231bc1eef4"
SRC_URI = "git://github.com/tpm2-software/tpm2-tss-engine.git;branch=v1.1.x"

inherit autotools-brokensep pkgconfig systemd

S = "${WORKDIR}/git"

PACKAGES += "${PN}-engines ${PN}-engines-staticdev ${PN}-bash-completion"

FILES_${PN}-dev = "${libdir}/engines-1.1/tpm2tss.so ${includedir}/*"
FILES_${PN}-engines = "${libdir}/engines-1.1/lib*.so*"
FILES_${PN}-engines-staticdev = "${libdir}/engines-1.1/libtpm2tss.a"
FILES_${PN}-bash-completion += "${datadir}/bash-completion/completions"
