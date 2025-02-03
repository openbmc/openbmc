DESCRIPTION = "luaposix is a POSIX binding for Lua."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/luaposix/luaposix"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f30d022f6ef53952fa87cc0b6fffb153"

DEPENDS += "lua-native lua virtual/crypt"

SRC_URI = "git://github.com/luaposix/luaposix.git;branch=release-v36.2;protocol=https \
"
SRCREV = "5a8d8c768fc3c51f42cb591e9523a60399efc6a1"
S = "${WORKDIR}/git"
LUA_VERSION = "5.4"

B = "${S}"

inherit pkgconfig

do_compile() {
    ${S}/build-aux/luke LUA_INCDIR=${STAGING_INCDIR}
}

do_install() {
    ${S}/build-aux/luke PREFIX=${D}${prefix} INST_LIBDIR=${D}${libdir}/lua/${LUA_VERSION} install
}

FILES:${PN} = "${datadir}/lua/${LUA_VERSION} \
               ${libdir}/lua/${LUA_VERSION}"
