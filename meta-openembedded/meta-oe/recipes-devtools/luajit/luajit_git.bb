SUMMARY = "Just-In-Time Compiler for Lua"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=abe9020de9c00909317d02d5c803810e"
HOMEPAGE = "http://luajit.org"

SRC_URI = "git://luajit.org/git/luajit-2.0.git;protocol=http;branch=v2.1 \
           file://0001-Do-not-strip-automatically-this-leaves-the-stripping.patch \
           file://clang.patch \
           "

# Set PV to a version tag and date (YYMMDD) associated with SRCREV if it is later.
PV = "2.1.0~beta3-210112"
SRCREV = "ec6edc5c39c25e4eb3fca51b753f9995e97215da"

S = "${WORKDIR}/git"

inherit pkgconfig binconfig siteinfo

BBCLASSEXTEND = "native"

# http://luajit.org/install.html#cross
# Host luajit needs to be compiled with the same pointer size
# If you want to cross-compile to any 32 bit target on an x64 OS,
# you need to install the multilib development package (e.g.
# libc6-dev-i386 on Debian/Ubuntu) and build a 32 bit host part
# (HOST_CC="gcc -m32").
BUILD_CC_ARCH_append = " ${@['-m32',''][d.getVar('SITEINFO_BITS') != '32']}"

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
    \
    'PREFIX=${prefix}' \
    'MULTILIB=${baselib}' \
    'LDCONFIG=:' \
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

# mips64/ppc/ppc64/riscv64 is not supported in this release
COMPATIBLE_HOST_mipsarchn32 = "null"
COMPATIBLE_HOST_mipsarchn64 = "null"
COMPATIBLE_HOST_powerpc = "null"
COMPATIBLE_HOST_powerpc64 = "null"
COMPATIBLE_HOST_powerpc64le = "null"
COMPATIBLE_HOST_riscv64 = "null"
COMPATIBLE_HOST_riscv32 = "null"
