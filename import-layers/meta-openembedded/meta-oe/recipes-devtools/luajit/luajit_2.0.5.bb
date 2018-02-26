SUMMARY = "Just-In-Time Compiler for Lua"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=10a96c93403affcc34765f4c2612bc22"
HOMEPAGE = "http://luajit.org"

SRC_URI = "http://luajit.org/download/LuaJIT-${PV}.tar.gz \
           file://0001-Do-not-strip-automatically-this-leaves-the-stripping.patch \
           file://clang.patch \
"
SRC_URI[md5sum] = "48353202cbcacab84ee41a5a70ea0a2c"
SRC_URI[sha256sum] = "874b1f8297c697821f561f9b73b57ffd419ed8f4278c82e05b48806d30c1e979"

S = "${WORKDIR}/LuaJIT-${PV}"

inherit pkgconfig binconfig siteinfo

BBCLASSEXTEND = "native"

# http://luajit.org/install.html#cross
# Host luajit needs to be compiled with the same pointer size
# If you want to cross-compile to any 32 bit target on an x64 OS,
# you need to install the multilib development package (e.g.
# libc6-dev-i386 on Debian/Ubuntu) and build a 32 bit host part
# (HOST_CC="gcc -m32").
BUILD_CC_ARCH_append = " ${@['-m32',''][d.getVar('SITEINFO_BITS', True) != '32']}"

# The lua makefiles expect the TARGET_SYS to be from uname -s
# Values: Windows, Linux, Darwin, iOS, SunOS, PS3, GNU/kFreeBSD
LUA_TARGET_OS = "Unknown"
LUA_TARGET_OS_darwin = "Darwin"
LUA_TARGET_OS_linux = "Linux"
LUA_TARGET_OS_linux-gnueabi = "Linux"
LUA_TARGET_OS_mingw32 = "Windows"

# We don't want the lua buildsystem's compiler optimizations, or its
# stripping, and we don't want it to pick up CFLAGS or LDFLAGS, as those apply
# to both host and target compiles
EXTRA_OEMAKE = "\
    Q= E='@:' \
    \
    CCOPT= CCOPT_x86= CFLAGS= LDFLAGS= TARGET_STRIP='@:' \
    \
    'TARGET_SYS=${LUA_TARGET_OS}' \
    \
    'CC=${CC}' \
    'TARGET_AR=${AR} rcus' \
    'TARGET_CFLAGS=${CFLAGS}' \
    'TARGET_LDFLAGS=${LDFLAGS}' \
    'TARGET_SHLDFLAGS=${LDFLAGS}' \
    'HOST_CC=${BUILD_CC}' \
    'HOST_CFLAGS=${BUILD_CFLAGS}' \
    'HOST_LDFLAGS=${BUILD_LDFLAGS}' \
    \
    'PREFIX=${prefix}' \
    'MULTILIB=${baselib}' \
"

do_compile () {
    oe_runmake
}

# There's INSTALL_LIB and INSTALL_SHARE also, but the lua binary hardcodes the
# '/share' and '/' + LUA_MULTILIB paths, so we don't want to break those
# expectations.
EXTRA_OEMAKEINST = "\
    'DESTDIR=${D}' \
    'INSTALL_BIN=${D}${bindir}' \
    'INSTALL_INC=${D}${includedir}/luajit-$(MAJVER).$(MINVER)' \
    'INSTALL_MAN=${D}${mandir}/man1' \
"
do_install () {
    oe_runmake ${EXTRA_OEMAKEINST} install
    rmdir ${D}${datadir}/lua/5.* \
          ${D}${datadir}/lua \
          ${D}${libdir}/lua/5.* \
          ${D}${libdir}/lua
}

PACKAGES += 'luajit-common'

# See the comment for EXTRA_OEMAKEINST. This is needed to ensure the hardcoded
# paths are packaged regardless of what the libdir and datadir paths are.
FILES_${PN} += "${prefix}/${baselib} ${prefix}/share"
FILES_${PN} += "${libdir}/libluajit-5.1.so.2 \
    ${libdir}/libluajit-5.1.so.${PV} \
"
FILES_${PN}-dev += "${libdir}/libluajit-5.1.a \
    ${libdir}/libluajit-5.1.so \
    ${libdir}/pkgconfig/luajit.pc \
"
FILES_luajit-common = "${datadir}/${BPN}-${PV}"

