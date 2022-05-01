SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"
SECTION = "tpm"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "c0b402f6a7b3456e8eb2445211e2d41c46c7e769e05fe4d8909ff64119f7a630"

inherit autotools pkgconfig bash-completion

do_configure:prepend() {
    # do not extract the version number from git
    sed -i -e 's/m4_esyscmd_s(\[git describe --tags --always --dirty\])/${PV}/' ${S}/configure.ac
}
