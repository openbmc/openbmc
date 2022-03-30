SUMMARY = "A top utility for I/O"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48e7be78bd2671d08c9c3bad71f1cfaa"

DEPENDS = "ncurses"

SRC_URI = "https://github.com/Tomas-M/iotop/releases/download/v1.21/iotop-1.21.tar.xz"
SRC_URI[sha256sum] = "7b4862ebc93909a3f800193140ca2464e926291a9c873b50dc31fa77e6d9383e"
UPSTREAM_CHECK_URI = "https://github.com/Tomas-M/iotop/releases"

inherit pkgconfig

EXTRA_OEMAKE = "V=1 STRIP=true"
# Fixes llvm-bc70b5.o: can't link soft-float modules with double-float modules
EXTRA_OEMAKE:append:toolchain-clang:riscv64 = " NO_FLTO=1"
EXTRA_OEMAKE:append:toolchain-clang:riscv32 = " NO_FLTO=1"

# Workaround BFD linker crash with clang on arm
# revisit when upgrading binutils and see if its fixed
LDFLAGS:append:toolchain-clang:arm = " -fuse-ld=lld"

do_install() {
    oe_runmake install DESTDIR=${D}
}
