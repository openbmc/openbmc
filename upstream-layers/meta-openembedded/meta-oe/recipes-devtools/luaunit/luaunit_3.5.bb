SUMMARY = "Unit testing framework for Lua"
HOMEPAGE = "https://github.com/bluebird75/luaunit"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3cbaf542c6e9a9dcad6c372227ac49a1"

SRC_URI = "git://github.com/bluebird75/luaunit.git;protocol=https;nobranch=1;tag=LUAUNIT_V3_5"
SRCREV = "9678c933e71cb26adcdfa6cf1baceb926398eb48"

RDEPENDS:${PN} = "lua"

LUA_VERSION = "5.5"

do_install() {
    install -D ${S}/luaunit.lua ${D}${datadir}/lua/${LUA_VERSION}/luaunit.lua
}

FILES:${PN} = "${datadir}/lua/${LUA_VERSION}/luaunit.lua"
