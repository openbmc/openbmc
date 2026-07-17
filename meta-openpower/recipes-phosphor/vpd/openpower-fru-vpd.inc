HOMEPAGE = "https://github.com/openbmc/openpower-vpd-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI += "git://github.com/openbmc/openpower-vpd-parser;branch=master;protocol=https"
SRCREV = "989d96682309c3d3e5a29fc981b09d1c08b56c00"
DEPENDS += "\
    nlohmann-json \
    cli11 \
"
