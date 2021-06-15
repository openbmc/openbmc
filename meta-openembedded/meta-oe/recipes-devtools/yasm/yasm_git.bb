SUMMARY = "x86 (SSE) assembler supporting NASM and GAS-syntaxes"
LICENSE = "MIT"
HOMEPAGE = "http://www.tortall.net/projects/yasm/"

LIC_FILES_CHKSUM = "file://COPYING;md5=a12d8903508fb6bfd49d8d82c6170dd9"

DEPENDS += "flex-native bison-native xmlto-native"

PV = "1.3.0+git${SRCPV}"
# v1.3.0
SRCREV = "ba463d3c26c0ece2e797b8d6381b161633b5971a"
SRC_URI = "git://github.com/yasm/yasm.git \
           file://0001-Do-not-use-AC_HEADER_STDC.patch \
"

S = "${WORKDIR}/git"

inherit autotools gettext python3native

CACHED_CONFIGUREVARS = "CCLD_FOR_BUILD='${CC_FOR_BUILD}'"

BBCLASSEXTEND = "native"

PARALLEL_MAKE = ""
