SUMMARY = "Provider for integration of TPM 2.0 to OpenSSL 3.0"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f4b4cb00f4d0d6807a0dc79759a57ac"

DEPENDS = "autoconf-archive-native tpm2-tss openssl"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "9a9aca55d4265ec501bcf9c56d21d6ca18dba902553f21c888fe725b42ea9964"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

inherit autotools pkgconfig

do_configure:prepend() {
    # do not extract the version number from git
    sed -i -e 's/m4_esyscmd_s(\[git describe --tags --always --dirty\])/${PV}/' ${S}/configure.ac
}

FILES:${PN} = "\
    ${libdir}/ossl-modules/tpm2.so"
