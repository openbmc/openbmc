require kpatch.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "7f550f01bd308cf058ae782327d29c8916cc5602"

PV = "0.6.1+git${SRCPV}"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
