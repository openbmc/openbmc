SUMMARY = "C-family abstract syntax tree XML output tool."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "2d52b47cfd88b703d3247f77bb7c3ad7372bc9cb"
SRC_URI = "git://github.com/CastXML/CastXML;protocol=https;branch=master;tag=v${PV}"

DEPENDS = "clang"

inherit cmake pkgconfig python3native

BBCLASSEXTEND = "native nativesdk"
