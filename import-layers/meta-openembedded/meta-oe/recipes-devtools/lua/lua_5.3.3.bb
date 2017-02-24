DESCRIPTION = "Lua is a powerful light-weight programming language designed \
for extending applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/readme.html;beginline=318;endline=352;md5=10ffd57d574c60d5b4d6189544e205a9"
HOMEPAGE = "http://www.lua.org/"

DEPENDS = "readline"
SRC_URI = "http://www.lua.org/ftp/lua-${PV}.tar.gz;name=tarballsrc \
           file://lua.pc.in \
           "
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', \
           'http://www.lua.org/tests/lua-${PV}-tests.tar.gz;name=tarballtest \
            file://run-ptest \
           ', '', d)}"

SRC_URI[tarballsrc.md5sum] = "703f75caa4fdf4a911c1a72e67a27498"
SRC_URI[tarballsrc.sha256sum] = "5113c06884f7de453ce57702abaac1d618307f33f6789fa870e87a59d772aca2"
SRC_URI[tarballtest.md5sum] = "76f4fb07f2a4970d554645ac26df86df"
SRC_URI[tarballtest.sha256sum] = "13154abc20976196119db531b4169ce1ce511755879d40b4192e4173291287e5"

inherit pkgconfig binconfig ptest

UCLIBC_PATCHES += "file://uclibc-pthread.patch"
SRC_URI_append_libc-uclibc = "${UCLIBC_PATCHES}"

TARGET_CC_ARCH += " -fPIC ${LDFLAGS}"
EXTRA_OEMAKE = "'CC=${CC} -fPIC' 'MYCFLAGS=${CFLAGS} -DLUA_USE_LINUX -fPIC' MYLDFLAGS='${LDFLAGS}'"

do_configure_prepend() {
    sed -i -e s:/usr/local:${prefix}:g src/luaconf.h
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
        'INSTALL_CMOD=${D}${libdir}/lua/5.3' \
        install
    install -d ${D}${libdir}/pkgconfig

    sed -e s/@VERSION@/${PV}/ ${WORKDIR}/lua.pc.in > ${WORKDIR}/lua.pc
    install -m 0644 ${WORKDIR}/lua.pc ${D}${libdir}/pkgconfig/
    rmdir ${D}${datadir}/lua/5.3
    rmdir ${D}${datadir}/lua
}

do_install_ptest () {
        cp -R --no-dereference --preserve=mode,links -v ${WORKDIR}/lua-${PV}-tests ${D}${PTEST_PATH}/test
}

BBCLASSEXTEND = "native"
