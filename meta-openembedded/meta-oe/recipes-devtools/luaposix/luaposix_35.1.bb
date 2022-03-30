DESCRIPTION = "luaposix is a POSIX binding for Lua."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/luaposix/luaposix"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f81069e00c0cad249f20efe958276db1"

DEPENDS += "lua-native lua virtual/crypt"

SRC_URI = "git://github.com/luaposix/luaposix.git;branch=release-v${PV};protocol=https \
"
SRCREV = "14043c5086ae738823a5dfbc9170d9e14193fbef"
S = "${WORKDIR}/git"
LUA_VERSION = "5.4"

B = "${S}"

inherit pkgconfig

do_compile() {
    ${S}/build-aux/luke
}

do_install() {
    ${S}/build-aux/luke PREFIX=${D}${prefix} INST_LIBDIR=${D}${libdir}/lua/${LUA_VERSION} install
}

FILES:${PN} = "${datadir}/lua/${LUA_VERSION} \
               ${libdir}/lua/${LUA_VERSION}"
