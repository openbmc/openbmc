SUMMARY = "Integrated Development Environment support"
DESCRIPTION = "Meta package for ensuring the build directory contains all appropriate toolchain packages for using an IDE"
LICENSE = "MIT"

DEPENDS = "virtual/libc gdb-cross-${TARGET_ARCH} qemu-native qemu-helper-native unfs3-native"
PR = "r3"
RM_WORK_EXCLUDE += "${PN}"

inherit meta toolchain-scripts nopackages

do_populate_ide_support () {
  toolchain_create_tree_env_script
}

addtask populate_ide_support before do_build after do_install
