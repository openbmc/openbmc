SUMMARY = "Phosphor python library"
DESCRIPTION = "Phosphor python library."
HOMEPAGE = "http://github.com/openbmc/pyobmc"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit setuptools

SRC_URI += "git://github.com/openbmc/pyphosphor"

SRCREV = "362fb80c081e114236cc5d29b166f45fd4539041"

S = "${WORKDIR}/git"
