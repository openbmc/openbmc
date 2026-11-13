SUMMARY = "Just-In-Time Compiler for Lua"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=a2c43bf4a9ea63755af2131b0ae59ff3"
HOMEPAGE = "http://luajit.org"

FORK ?= "git://luajit.org/git/luajit-2.0.git;protocol=http;branch=v2.1"
FORK:riscv64 = "git://github.com/plctlab/LuaJIT.git;protocol=https;branch=v2.1-riscv64"
SRC_URI = "${FORK} \
           file://0001-Do-not-strip-automatically-this-leaves-the-stripping.patch \
           file://0001-Use-builtin-for-clear_cache.patch \
           "

PV = "2.1"
SRCREV = "659a61693aa3b87661864ad0f12eee14c865cd7f"
SRCREV:riscv64 = "2c509ee67e20772bce3a11ce8f53b6b14b4980bb"

# The v2.1 branch is rolling with no tags; track commits.
UPSTREAM_CHECK_COMMITS = "1"

inherit pkgconfig binconfig siteinfo qemu

BBCLASSEXTEND = "native"

# http://luajit.org/install.html#cross
# The build compiles helpers (minilua, buildvm) and runs them, and buildvm
# must have the target's pointer size. For a 32 bit target on an x86 build
# host they are built with "gcc -m32", which needs the multilib development
# package (e.g. libc6-dev:i386 on Debian/Ubuntu). Other build hosts have no
# -m32, so there the helpers are built with the target compiler and run under
# qemu-user instead, which needs a machine qemu-user can emulate
# (qemu-usermode in MACHINE_FEATURES).
LUAJIT_HOST_QEMU = "${@'1' if d.getVar('SITEINFO_BITS') == '32' and d.getVar('BUILD_ARCH') not in ('x86_64', 'i686') else ''}"
LUAJIT_HOST_QEMU:class-native = ""

BUILD_CC_ARCH:append = "${@' -m32' if d.getVar('SITEINFO_BITS') == '32' and not d.getVar('LUAJIT_HOST_QEMU') else ''}"
DEPENDS:append = "${@' qemu-native' if d.getVar('LUAJIT_HOST_QEMU') else ''}"

LUAJIT_HOST_OEMAKE = "'HOST_CC=${BUILD_CC}' 'HOST_CFLAGS=${BUILD_CFLAGS}'"
LUAJIT_HOST_OEMAKE_QEMU = "\
    'HOST_CC=${CC}' 'HOST_CFLAGS=${CFLAGS}' 'HOST_LDFLAGS=${LDFLAGS}' \
    'MINILUA_X=${WORKDIR}/luajit-qemuwrapper host/minilua' \
    'BUILDVM_X=${WORKDIR}/luajit-qemuwrapper host/buildvm' \
"

# The lua makefiles expect the TARGET_SYS to be from uname -s
# Values: Windows, Linux, Darwin, iOS, SunOS, PS3, GNU/kFreeBSD
LUA_TARGET_OS = "Unknown"
LUA_TARGET_OS:darwin = "Darwin"
LUA_TARGET_OS:linux = "Linux"
LUA_TARGET_OS:linux-gnueabi = "Linux"
LUA_TARGET_OS:linux-musl = "Linux"
LUA_TARGET_OS:mingw32 = "Windows"

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
    ${@d.getVar('LUAJIT_HOST_OEMAKE_QEMU') if d.getVar('LUAJIT_HOST_QEMU') else d.getVar('LUAJIT_HOST_OEMAKE')} \
    \
    'PREFIX=${prefix}' \
    'MULTILIB=${baselib}' \
    'LDCONFIG=:' \
"

do_compile () {
    if [ -n "${LUAJIT_HOST_QEMU}" ]; then
        cat > ${WORKDIR}/luajit-qemuwrapper <<EOF
#!/bin/sh
${@qemu_wrapper_cmdline(d, '${STAGING_DIR_HOST}', ['${STAGING_DIR_HOST}${libdir}', '${STAGING_DIR_HOST}${base_libdir}'])} "\$@"
EOF
        chmod +x ${WORKDIR}/luajit-qemuwrapper
    fi
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
FILES:${PN} += "${prefix}/${baselib} ${prefix}/share"
FILES:${PN} += "${libdir}/libluajit-5.1.so.2 \
    ${libdir}/libluajit-5.1.so.${PV} \
"
FILES:${PN}-dev += "${libdir}/libluajit-5.1.a \
    ${libdir}/libluajit-5.1.so \
    ${libdir}/pkgconfig/luajit.pc \
"
FILES:luajit-common = "${datadir}/${BPN}-${PV}"

# ppc64/riscv32 is not supported in this release
COMPATIBLE_HOST:powerpc64 = "null"
COMPATIBLE_HOST:powerpc64le = "null"
COMPATIBLE_HOST:riscv32 = "null"

CVE_STATUS[CVE-2024-25176] = "fixed-version: The used revision contains the fix already."
CVE_STATUS[CVE-2024-25177] = "fixed-version: The used revision contains the fix already."
CVE_STATUS[CVE-2024-25178] = "fixed-version: The used revision contains the fix already."
