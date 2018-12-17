SUMMARY = "C++/Boost Asio based websocket client/server library."
SECTION = "libs/network"
HOMEPAGE = "https://github.com/zaphoyd/websocketpp"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=4d168d763c111f4ffc62249870e4e0ea"
DEPENDS = "openssl boost zlib"

SRC_URI = "git://github.com/zaphoyd/websocketpp.git;protocol=https;branch=master"

# tag 0.8.1
SRCREV= "c6d7e295bf5a0ab9b5f896720cc1a0e0fdc397a7"

S = "${WORKDIR}/git"

inherit cmake
