DESCRIPTION = "Lua is a powerful light-weight programming language designed \
for extending applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/readme.html;beginline=318;endline=352;md5=68fc2aa2b751a37ff265524ddf026d7f"
HOMEPAGE = "http://www.lua.org/"

PR = "r0"

DEPENDS = "readline"
SRC_URI = "http://www.lua.org/ftp/lua-${PV}.tar.gz;name=tarballsrc \
           file://lua.pc.in \
           "
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', \
           'http://www.lua.org/tests/lua-${PV}-tests.tar.gz;name=tarballtest \
            file://run-ptest \
           ', '', d)}"

SRC_URI[tarballsrc.md5sum] = "33278c2ab5ee3c1a875be8d55c1ca2a1"
SRC_URI[tarballsrc.sha256sum] = "c740c7bb23a936944e1cc63b7c3c5351a8976d7867c5252c8854f7b2af9da68f"
SRC_URI[tarballtest.md5sum] = "a2b7ab1b8ff82a0145376e233ef30a4a"
SRC_URI[tarballtest.sha256sum] = "56909863a3713dee3709b3dbd0c868237e4f5c9ea1744f5bf0ba8bafa6c4ed32"

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
