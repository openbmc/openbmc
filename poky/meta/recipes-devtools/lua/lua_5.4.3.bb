DESCRIPTION = "Lua is a powerful light-weight programming language designed \
for extending applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/readme.html;beginline=307;endline=330;md5=79c3f6b19ad05efe24c1681f025026bb"
HOMEPAGE = "http://www.lua.org/"

SRC_URI = "http://www.lua.org/ftp/lua-${PV}.tar.gz;name=tarballsrc \
           file://lua.pc.in \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'http://www.lua.org/tests/lua-${PV_testsuites}-tests.tar.gz;name=tarballtest file://run-ptest ', '', d)} \
           file://74d99057a5146755e737c479850f87fd0e3b6868.patch \
           "

# if no test suite matches PV release of Lua exactly, download the suite for the closest Lua release.
PV_testsuites = "5.4.3"

SRC_URI[tarballsrc.sha256sum] = "f8612276169e3bfcbcfb8f226195bfc6e466fe13042f1076cbde92b7ec96bbfb"
SRC_URI[tarballtest.sha256sum] = "5d29c3022897a8290f280ebe1c6853248dfa35a668e1fc02ba9c8cde4e7bf110"

# remove at next version upgrade or when output changes
# was added after intermittent repro failures poisoned the cache
PR = "r1"
HASHEQUIV_HASH_VERSION .= ".2"

inherit pkgconfig binconfig ptest

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = ",,readline"

TARGET_CC_ARCH += " -fPIC ${LDFLAGS}"
EXTRA_OEMAKE = "'CC=${CC} -fPIC' 'MYCFLAGS=${CFLAGS} -fPIC' MYLDFLAGS='${LDFLAGS}' 'AR=ar rcD' 'RANLIB=ranlib -D'"

do_configure:prepend() {
    sed -i -e s:/usr/local:${prefix}:g src/luaconf.h
    sed -i -e s:lib/lua/:${baselib}/lua/:g src/luaconf.h
}

do_compile () {
    oe_runmake ${@bb.utils.contains('PACKAGECONFIG', 'readline', 'linux-readline', 'linux', d)}
}

do_install () {
    oe_runmake \
        'INSTALL_TOP=${D}${prefix}' \
        'INSTALL_BIN=${D}${bindir}' \
        'INSTALL_INC=${D}${includedir}/' \
        'INSTALL_MAN=${D}${mandir}/man1' \
        'INSTALL_SHARE=${D}${datadir}/lua' \
        'INSTALL_LIB=${D}${libdir}' \
        'INSTALL_CMOD=${D}${libdir}/lua/5.4' \
        install
    install -d ${D}${libdir}/pkgconfig

    sed -e s/@VERSION@/${PV}/ ${WORKDIR}/lua.pc.in > ${WORKDIR}/lua.pc
    install -m 0644 ${WORKDIR}/lua.pc ${D}${libdir}/pkgconfig/
    rmdir ${D}${datadir}/lua/5.4
    rmdir ${D}${datadir}/lua
}

do_install_ptest () {
        cp -R --no-dereference --preserve=mode,links -v ${WORKDIR}/lua-${PV_testsuites}-tests ${D}${PTEST_PATH}/test
}

BBCLASSEXTEND = "native nativesdk"
