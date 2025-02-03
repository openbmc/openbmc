SUMMARY = "A top utility for I/O"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3154adaa1a9ca0d8dcc1944518ece1b9"

DEPENDS = "ncurses"

SRC_URI = "git://github.com/Tomas-M/iotop.git;branch=master;protocol=https"
SRCREV = "4e32789e496a027201e66b5773a9d7955f7c638b"

S = "${WORKDIR}/git"

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
