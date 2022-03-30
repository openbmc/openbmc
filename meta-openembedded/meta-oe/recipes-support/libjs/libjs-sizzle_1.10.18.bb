SUMMARY = "Pure-JavaScript CSS selector engine"
HOMEPAGE = "https://github.com/jquery/sizzle/wiki"
LICENSE = "GPL-2.0-only & MIT & AFL-2.1"
LIC_FILES_CHKSUM = "file://MIT-LICENSE.txt;md5=e43aa437a6a1ba421653bd5034333bf9"

SRC_URI = "http://kr.archive.ubuntu.com/ubuntu/pool/universe/s/sizzle/sizzle_1.10.18.orig.tar.gz"
SRC_URI[md5sum] = "91477c1edeef9f8100ffd6c4d31725b5"
SRC_URI[sha256sum] = "8e04ab84bb74b2e338dffc63cd2e52b007f1d8af01b3d25da4d2e07f2b5890f8"

S = "${WORKDIR}/sizzle-${PV}"

SIZZLEDIR = "${S}/dist"

do_install() {
    install -d -m 0755 ${D}/${datadir}/javascript/sizzle/
    install -m 0644 ${SIZZLEDIR}/*.js ${D}/${datadir}/javascript/sizzle/
}

FILES:${PN} = "${datadir}/javascript/sizzle/"
