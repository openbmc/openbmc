SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91b7c548d73ea16537799e8060cea819"
SECTION = "tpm"

DEPENDS = "pkgconfig tpm2-tss openssl curl autoconf-archive"

SRCREV = "a17daa948fc67685651bf3b7a589ed341080ddd3"

SRC_URI = "git://github.com/tpm2-software/tpm2-tools.git;branch=3.X"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
