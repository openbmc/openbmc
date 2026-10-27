HOMEPAGE = "https://github.com/openbmc/openpower-vpd-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI += "git://github.com/openbmc/openpower-vpd-parser;branch=master;protocol=https"
SRCREV = "e29c0f0eb38b6c21c02b4afbcae305715a0d07ce"
DEPENDS += "\
    nlohmann-json \
    cli11 \
"
