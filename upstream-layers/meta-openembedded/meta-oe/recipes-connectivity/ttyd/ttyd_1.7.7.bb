SUMMARY = "ttyd is a simple command-line tool for sharing terminal over the web."
SECTION = "console/network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fe5f001c65f923d49dc96cce96ce935"

SRC_URI = "git://github.com/tsl0922/ttyd.git;protocol=https;branch=main;tag=${PV}"
SRCREV = "40e79c706be14029b391f369bee6613c31667abb"

inherit cmake

DEPENDS = "libuv json-c zlib libwebsockets"
