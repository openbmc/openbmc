SUMMARY     = "To get required hardware procedure attribute xml files"
DESCRIPTION = "Copy all the required hardware procedures attributes xml file \
with respective directory structures"

PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE_PROLOG;md5=d8e5f403c98fd80dcea90b9cc8cd083c"

require ekb.inc

SRC_URI = "${EKB_URI}"
SRCREV = "${EKB_REV}"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

do_install() {

    mkdir -p ${D}${datadir}/${BPN}

    # Copying all required hwp's attributes xml file with respective directory structures
    (cd ${S} && cp --parents ${REQ_ATTRS_XMLS} ${D}${datadir}/${BPN})
}
