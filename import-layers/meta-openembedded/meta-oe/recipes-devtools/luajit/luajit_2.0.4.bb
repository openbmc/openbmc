SUMMARY = "Just-In-Time Compiler for Lua"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=3992f1fbae3b8b061f9056b7fcda8cc6"
HOMEPAGE = "http://luajit.org"

SRC_URI = "http://luajit.org/download/LuaJIT-${PV}.tar.gz \
           file://0001-Do-not-strip-automatically-this-leaves-the-stripping.patch \
"
SRC_URI[md5sum] = "dd9c38307f2223a504cbfb96e477eca0"
SRC_URI[sha256sum] = "620fa4eb12375021bef6e4f237cbd2dd5d49e56beb414bee052c746beef1807d"

S = "${WORKDIR}/LuaJIT-${PV}"

inherit pkgconfig binconfig

BBCLASSEXTEND = "native"

do_configure_prepend() {
    sed -i 's:PREFIX= /usr/local:PREFIX= ${prefix}:g' ${S}/Makefile
    sed -i 's:MULTILIB= lib:MULTILIB= ${baselib}:g' ${S}/Makefile
}

# http://luajit.org/install.html#cross
# Host luajit needs to be compiled with the same pointer size
# If you want to cross-compile to any 32 bit target on an x64 OS,
# you need to install the multilib development package (e.g.
# libc6-dev-i386 on Debian/Ubuntu) and build a 32 bit host part
# (HOST_CC="gcc -m32").
BUILD_CC_ARCH_append_powerpc = ' -m32'
BUILD_CC_ARCH_append_x86 = ' -m32'
BUILD_CC_ARCH_append_arm = ' -m32'

EXTRA_OEMAKE_append_class-target = '\
    CROSS=${HOST_PREFIX} \
    HOST_CC="${BUILD_CC} ${BUILD_CC_ARCH}" \
    TARGET_CFLAGS="${TOOLCHAIN_OPTIONS} ${TARGET_CC_ARCH}" \
    TARGET_LDFLAGS="${TOOLCHAIN_OPTIONS}" \
    TARGET_SHLDFLAGS="${TOOLCHAIN_OPTIONS}" \
'

do_compile () {
    oe_runmake
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install
    rmdir ${D}${datadir}/lua/5.* \
          ${D}${datadir}/lua \
          ${D}${libdir}/lua/5.* \
          ${D}${libdir}/lua
}

PACKAGES += 'luajit-common'

FILES_${PN} += "${libdir}/libluajit-5.1.so.2 \
    ${libdir}/libluajit-5.1.so.${PV} \
"
FILES_${PN}-dev += "${libdir}/libluajit-5.1.a \
    ${libdir}/libluajit-5.1.so \
    ${libdir}/pkgconfig/luajit.pc \
"
FILES_luajit-common = "${datadir}/${BPN}-${PV}"

