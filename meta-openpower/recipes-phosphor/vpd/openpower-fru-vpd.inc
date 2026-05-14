HOMEPAGE = "https://github.com/openbmc/openpower-vpd-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI += "git://github.com/openbmc/openpower-vpd-parser;branch=master;protocol=https"
SRCREV = "8a7a8dde277e5b42088f0d9d394d3703a7e7a6a2"
DEPENDS += "\
    nlohmann-json \
    cli11 \
"
