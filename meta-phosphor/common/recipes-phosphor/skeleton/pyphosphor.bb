SUMMARY = "Phosphor python library"
DESCRIPTION = "Phosphor python library."
HOMEPAGE = "http://github.com/openbmc/pyphosphor"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit setuptools

SRC_URI += "git://github.com/openbmc/pyphosphor"

SRCREV = "ecf8bd8f38d93fda3d08df771709bd5289a480e9"

S = "${WORKDIR}/git"
