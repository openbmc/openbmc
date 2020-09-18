SUMMARY = "Linux firmware validation framework" 
DESCRIPTION = "Cukinia is designed to help Linux-based embedded firmware \
developers run simple system-level validation tests on their firmware." 
HOMEPAGE = "https://github.com/savoirfairelinux/cukinia"
LICENSE = "GPLv3 & Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "git://github.com/savoirfairelinux/cukinia.git;protocol=https;branch=master"

SRCREV = "ea934c5393ad99c37bec3c621a364122720cce02"

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/cukinia ${D}${sbindir}
}
