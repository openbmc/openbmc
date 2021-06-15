DESCRIPTION = "luaposix is a POSIX binding for Lua."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/luaposix/luaposix"
LIC_FILES_CHKSUM = "file://COPYING;md5=7dd2aad04bb7ca212e69127ba8d58f9f"

DEPENDS += "lua-native lua"

SRC_URI = "git://github.com/luaposix/luaposix.git;branch=release \
           file://0001-fix-avoid-race-condition-between-test-and-mkdir.patch \
"
SRCREV = "8e4902ed81c922ed8f76a7ed85be1eaa3fd7e66d"
S = "${WORKDIR}/git"
LUA_VERSION = "5.3"

inherit autotools pkgconfig

do_install() {
    oe_runmake 'DESTDIR=${D}' 'luadir=${datadir}/lua/${LUA_VERSION}' 'luaexecdir=${libdir}/lua/${LUA_VERSION}' install
}

FILES_${PN} = "${datadir}/lua/${LUA_VERSION} ${libdir}/lua/${LUA_VERSION}"
