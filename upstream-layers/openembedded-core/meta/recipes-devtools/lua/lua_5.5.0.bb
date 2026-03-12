SUMMARY = "Lua is a powerful light-weight programming language designed \
for extending applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/readme.html;beginline=299;endline=320;md5=0e573c143cb6491b41cf02cfbcb8c267"
HOMEPAGE = "http://www.lua.org/"

SRC_URI = "http://www.lua.org/ftp/lua-${PV}.tar.gz;name=tarballsrc \
           file://lua.pc.in \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'http://www.lua.org/tests/lua-${PV_testsuites}-tests.tar.gz;name=tarballtest file://run-ptest ', '', d)} \
           "

# if no test suite matches PV release of Lua exactly, download the suite for the closest Lua release.
PV_testsuites = "${PV}"

SRC_URI[tarballsrc.sha256sum] = "57ccc32bbbd005cab75bcc52444052535af691789dba2b9016d5c50640d68b3d"
SRC_URI[tarballtest.sha256sum] = "5e47bbfad7db2965d69580e918ee64edeb8d8d32de404b8dae9ce5c6d76a1472"

inherit pkgconfig binconfig ptest

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = ",,readline,readline"

TARGET_CC_ARCH += " -fPIC ${LDFLAGS}"
EXTRA_OEMAKE = "'CC=${CC} -fPIC' 'MYCFLAGS=${CFLAGS} -fPIC' MYLDFLAGS='${LDFLAGS}' 'AR=ar rcD' 'RANLIB=ranlib -D'"

do_configure:prepend:class-target() {
    libreadline=$(find "${RECIPE_SYSROOT}" -name libreadline.so)
    if [ -n "$libreadline" ] && [ -L "$libreadline" ]; then
        real_libreadline=$(readlink "$libreadline")
        sed -i -e "s/#define LUA_READLINELIB[[:space:]]*\"libreadline.*$/#define LUA_READLINELIB     \"$real_libreadline\"/g" src/luaconf.h
    fi
}

do_configure:prepend() {
    sed -i -e s:/usr/local:${prefix}:g src/luaconf.h
    sed -i -e s:lib/lua/:${baselib}/lua/:g src/luaconf.h
}

do_compile () {
    oe_runmake linux
}

do_install () {
    oe_runmake \
        'INSTALL_TOP=${D}${prefix}' \
        'INSTALL_BIN=${D}${bindir}' \
        'INSTALL_INC=${D}${includedir}/' \
        'INSTALL_MAN=${D}${mandir}/man1' \
        'INSTALL_SHARE=${D}${datadir}/lua' \
        'INSTALL_LIB=${D}${libdir}' \
        'INSTALL_CMOD=${D}${libdir}/lua/5.5' \
        install
    install -d ${D}${libdir}/pkgconfig

    sed -e s/@VERSION@/${PV}/ -e s#@LIBDIR@#${libdir}# -e s#@INCLUDEDIR@#${includedir}# ${UNPACKDIR}/lua.pc.in > ${S}/lua.pc
    install -m 0644 ${S}/lua.pc ${D}${libdir}/pkgconfig/
    rmdir ${D}${datadir}/lua/5.*
    rmdir ${D}${datadir}/lua
}

do_install_ptest () {
        cp -R --no-dereference --preserve=mode,links -v ${UNPACKDIR}/lua-${PV_testsuites}-tests ${D}${PTEST_PATH}/test
}

do_install_ptest:append:libc-musl () {
        # locale tests does not work on musl, due to limited locale implementation
        # https://wiki.musl-libc.org/open-issues.html#Locale-limitations
        sed -i -e 's|os.setlocale("pt_BR") or os.setlocale("ptb")|false|g' ${D}${PTEST_PATH}/test/literals.lua
}

BBCLASSEXTEND = "native nativesdk"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}-dev:${includedir}/luaconf.h"
