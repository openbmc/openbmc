LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"
require make.inc

SRC_URI += " \
           file://0001-m4-getloadavg.m4-restrict-AIX-specific-test-on-AIX.patch \
           file://sigpipe.patch \
           "

EXTRA_OECONF += "--without-guile"

SRC_URI[sha256sum] = "581f4d4e872da74b3941c874215898a7d35802f03732bdccee1d4a7979105d18"

BBCLASSEXTEND = "native nativesdk"
