SUMMARY = "x86 (SSE) assembler supporting NASM and GAS-syntaxes"
LICENSE = "BSD"
HOMEPAGE = "http://www.tortall.net/projects/yasm/"

LIC_FILES_CHKSUM = "file://COPYING;md5=a12d8903508fb6bfd49d8d82c6170dd9"

SRC_URI = "http://www.tortall.net/projects/yasm/releases/${BP}.tar.gz"

SRC_URI[md5sum] = "fc9e586751ff789b34b1f21d572d96af"
SRC_URI[sha256sum] = "3dce6601b495f5b3d45b59f7d2492a340ee7e84b5beca17e48f862502bd5603f"

export CCLD_FOR_BUILD = "${CC_FOR_BUILD}"

inherit autotools gettext

BBCLASSEXTEND = "native"
