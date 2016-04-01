SUMMARY = "Integrated Development Environment support"
DESCRIPTION = "Meta package for ensuring the build directory contains all appropriate toolchain packages for using an IDE"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

DEPENDS = "virtual/libc gdb-cross-${TARGET_ARCH} qemu-native qemu-helper-native unfs3-native"
PR = "r3"

inherit meta toolchain-scripts

do_populate_ide_support () {
  toolchain_create_tree_env_script
}

addtask populate_ide_support before do_build after do_install
