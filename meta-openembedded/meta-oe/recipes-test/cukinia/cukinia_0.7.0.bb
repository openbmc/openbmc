SUMMARY = "Linux firmware validation framework"
DESCRIPTION = "Cukinia is designed to help Linux-based embedded firmware \
developers run simple system-level validation tests on their firmware."
HOMEPAGE = "https://github.com/savoirfairelinux/cukinia"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "git://github.com/savoirfairelinux/cukinia.git;protocol=https;branch=master"

SRCREV = "be56f653743cc0e68bef81ef35df7c50ff8919c4"

S = "${WORKDIR}/git"

RRECOMMENDS:${PN} = "libgpiod-tools"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/cukinia ${D}${sbindir}
}
