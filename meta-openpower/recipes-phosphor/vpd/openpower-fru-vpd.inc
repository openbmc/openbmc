HOMEPAGE = "https://github.com/openbmc/openpower-vpd-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRC_URI += "git://github.com/openbmc/openpower-vpd-parser;branch=master;protocol=https"
SRCREV = "2349ce3c7843c4d70d8daa64c9cec1c681647d96"
DEPENDS += "\
    nlohmann-json \
    cli11 \
"
