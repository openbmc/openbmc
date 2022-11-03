SUMMARY = "Provider for integration of TPM 2.0 to OpenSSL 3.0"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b75785ac083d3c3ca04d99d9e4e1fbab"

DEPENDS = "autoconf-archive-native tpm2-tss openssl"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "5a9bb0c6c61d026272b8843cbc291b5dfa9a55c1661a513b1c980807ad2dad01"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

inherit autotools pkgconfig

do_configure:prepend() {
    # do not extract the version number from git
    sed -i -e 's/m4_esyscmd_s(\[git describe --tags --always --dirty\])/${PV}/' ${S}/configure.ac
}

FILES:${PN} = "\
    ${libdir}/ossl-modules/tpm2.so"
