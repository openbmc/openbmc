DESCRIPTION = "luaposix is a POSIX binding for Lua."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/luaposix/luaposix"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9a2c76dbe2ca56cf57296af9c9995c27"

DEPENDS += "lua-native lua virtual/crypt"

SRC_URI = "git://github.com/luaposix/luaposix.git;nobranch=1;tag=v${PV};protocol=https \
"
SRCREV = "e6b94b37c19c4bd19a7dc475a4f4cb56f61f5da9"
LUA_VERSION = "5.5"

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
