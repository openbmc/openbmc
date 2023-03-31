LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"
require make.inc

SRC_URI += " \
           file://0001-m4-getloadavg.m4-restrict-AIX-specific-test-on-AIX.patch \
           "

EXTRA_OECONF += "--without-guile"

SRC_URI[sha256sum] = "dd16fb1d67bfab79a72f5e8390735c49e3e8e70b4945a15ab1f81ddb78658fb3"

BBCLASSEXTEND = "native nativesdk"
