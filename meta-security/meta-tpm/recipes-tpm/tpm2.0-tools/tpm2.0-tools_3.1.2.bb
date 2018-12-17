SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2.0-tools"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91b7c548d73ea16537799e8060cea819"
SECTION = "tpm"

DEPENDS = "pkgconfig tpm2.0-tss openssl curl autoconf-archive"

SRCREV = "5e2f1aafc58e60c5050f85147a14914561f28ad9"

SRC_URI = "git://github.com/01org/tpm2.0-tools.git;name=tpm2.0-tools;destsuffix=tpm2.0-tools;branch=3.X"

S = "${WORKDIR}/tpm2.0-tools"

inherit autotools pkgconfig
