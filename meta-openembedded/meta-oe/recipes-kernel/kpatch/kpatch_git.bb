require kpatch.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "d7c5810e2a6acacfa5fec1e38d2f75af8e8c818c"

PV = "0.7.1+git${SRCPV}"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
