require kpatch.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "db6efbb8c7e90d2b761272cf563047119072768f"

PV = "0.5.0+git${SRCPV}"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
