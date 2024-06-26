SUMMARY = "Google Key installation Script"
DESCRIPTION = "Google Key installation Script"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "gnupg-gpg"

SRC_URI += " \
 file://platforms_gbmc_bringup.gpg \
 file://platforms_gbmc_secure.gpg \
 file://verify-bmc-image.sh \
"

do_install() {
    # Install keys into image.
    install -d -m 0755 ${D}${datadir}/google-key
    install -m 0644 ${UNPACKDIR}/platforms_gbmc_secure.gpg ${D}${datadir}/google-key/prod.key
    install -m 0644 ${UNPACKDIR}/platforms_gbmc_bringup.gpg ${D}${datadir}/google-key/dev.key

    # Install the verification helper
    install -d -m 0755 ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/verify-bmc-image.sh ${D}${bindir}
}
