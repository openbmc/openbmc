SUMMARY = "The tpm2-tss-engine project implements a cryptographic engine for OpenSSL." 
DESCRIPTION = "The tpm2-tss-engine project implements a cryptographic engine for OpenSSL for Trusted Platform Module (TPM 2.0) using the tpm2-tss software stack that follows the Trusted Computing Groups (TCG) TPM Software Stack (TSS 2.0). It uses the Enhanced System API (ESAPI) interface of the TSS 2.0 for downwards communication. It supports RSA decryption and signatures as well as ECDSA signatures."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b3ab643b9ce041de515d1ed092a36d4"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive-native bash-completion libtss2 libgcrypt openssl"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Configure-Allow-disabling-of-digest-sign-operations.patch \
           file://0002-Fix-mismatch-of-OpenSSL-function-signatures-that-cau.patch \
           "

SRC_URI[sha256sum] = "3c94fef110dd3630b3c28c5875febba76b7d5ba2fcc04a14c4a30f5d2157c265"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

inherit autotools-brokensep pkgconfig systemd

# It uses the API deprecated since the OpenSSL 3.0
CFLAGS:append = ' -Wno-deprecated-declarations -Wno-unused-parameter'

do_configure:prepend() {
    # do not extract the version number from git
    sed -i -e 's/m4_esyscmd_s(\[git describe --tags --always --dirty\])/${PV}/' ${S}/configure.ac
}

PACKAGES += "${PN}-engines ${PN}-engines-staticdev ${PN}-bash-completion"

FILES:${PN}-dev = "${libdir}/engines-3/tpm2tss.so ${includedir}/*"
FILES:${PN}-engines = "${libdir}/engines-3/lib*.so*"
FILES:${PN}-engines-staticdev = "${libdir}/engines-3/libtpm2tss.a"
FILES:${PN}-bash-completion += "${datadir}/bash-completion/completions"
