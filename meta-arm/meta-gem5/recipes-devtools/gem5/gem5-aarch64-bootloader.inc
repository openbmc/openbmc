# Build instructions for gem5 custom bootloader

SUMMARY = "Gem5 AARCH64 boot loader"
LICENSE = "BSD-3-Clause"

inherit deploy

PROVIDES += "virtual/gem5-bootloader"

COMPATIBLE_MACHINE = "gem5-arm64"

BOOTLOADER_SRC_PATH ?= "${S}/system/arm/bootloader/arm64"

# no configure step
do_configure[noexec] = "1"

# no install
do_install[noexec] = "1"

do_compile() {
    oe_runmake -C ${BOOTLOADER_SRC_PATH} all CROSS_COMPILE=${TARGET_PREFIX}
}

do_deploy() {
    oe_runmake -C ${BOOTLOADER_SRC_PATH} install \
        CROSS_COMPILE=${TARGET_PREFIX} DESTDIR=${DEPLOYDIR}/binaries
}

addtask deploy before do_build after do_compile

