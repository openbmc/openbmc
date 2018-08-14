SUMMARY = "C++/Boost Asio based websocket client/server library."
SECTION = "libs/network"
HOMEPAGE = "https://github.com/zaphoyd/websocketpp"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=4d168d763c111f4ffc62249870e4e0ea"
DEPENDS = "openssl boost zlib"

SRC_URI = "git://github.com/zaphoyd/websocketpp.git;protocol=https;branch=master"

# tag 0.7.0
SRCREV= "378437aecdcb1dfe62096ffd5d944bf1f640ccc3"

SRC_URI += "file://0001-Fix-issue-599.patch \
            file://9ddb300d874a30db35e3ad58f188944bef0bf31b.patch \
            file://4cab5e5c0c5f19fcee7d37b4a38b156d63a150d4.patch \
            file://disable-tests.patch \
 "

S = "${WORKDIR}/git"

inherit cmake
