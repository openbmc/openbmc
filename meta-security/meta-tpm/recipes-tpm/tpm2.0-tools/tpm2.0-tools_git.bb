SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2.0-tools"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91b7c548d73ea16537799e8060cea819"
SECTION = "tpm"

DEPENDS = "pkgconfig tpm2.0-tss openssl curl autoconf-archive"

# July 10, 2017
SRCREV = "26c0557040c1cf8107fa3ebbcf2a5b07cc84b881"

SRC_URI = "git://github.com/01org/tpm2.0-tools.git;name=tpm2.0-tools;destsuffix=tpm2.0-tools"

S = "${WORKDIR}/tpm2.0-tools"

PV = "2.0.0+git${SRCPV}"

inherit autotools pkgconfig
