HOMEPAGE = "https://github.com/openbmc/openpower-vpd-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI += "git://github.com/openbmc/openpower-vpd-parser;branch=master;protocol=https"
SRCREV = "922b6223aa9f0c9fae78de6c5fa80fe815a9b04c"
DEPENDS += "\
    nlohmann-json \
    cli11 \
"
