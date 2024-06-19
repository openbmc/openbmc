SUMMARY = "Wrapper for tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://yoctoproject.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://dt-doc-validate \
           file://dt-mk-schema \
           file://dt-validate"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -d ${D}${bindir}/
    install -m 755 ${UNPACKDIR}/dt-doc-validate ${D}${bindir}/
    install -m 755 ${UNPACKDIR}/dt-mk-schema ${D}${bindir}/
    install -m 755 ${UNPACKDIR}/dt-validate ${D}${bindir}/
}

BBCLASSEXTEND = "native nativesdk"
