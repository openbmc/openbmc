SUMMARY = "Insecure private key for testing and development"
DESCRIPTION = "Do not use this key to sign images."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI += "file://OpenBMC.priv"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit native

do_install() {
        bbplain "Using an insecure image signing key!"
        install -d ${D}${datadir}
        install -m 400 ${UNPACKDIR}/OpenBMC.priv ${D}${datadir}
}
