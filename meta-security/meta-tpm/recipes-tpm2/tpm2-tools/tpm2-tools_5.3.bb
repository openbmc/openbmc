SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"
SECTION = "tpm"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "e7ce2fd36ef5cdbd7872d823a442e8754a4f0ca7c54b60efcdb75c12a1f98f8f"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

inherit autotools pkgconfig bash-completion

do_configure:prepend() {
    # do not extract the version number from git
    sed -i -e 's/m4_esyscmd_s(\[git describe --tags --always --dirty\])/${PV}/' ${S}/configure.ac
}

# need tss-esys
RDEPENDS:${PN} = "libtss2 tpm2-abrmd"
