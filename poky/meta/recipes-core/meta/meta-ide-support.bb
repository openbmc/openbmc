SUMMARY = "Integrated Development Environment support"
DESCRIPTION = "Meta package for ensuring the build directory contains all appropriate toolchain packages for using an IDE"
LICENSE = "MIT"

DEPENDS = "virtual/libc gdb-cross-${TARGET_ARCH} qemu-native qemu-helper-native unfs3-native cmake-native"
PR = "r3"
RM_WORK_EXCLUDE += "${PN}"

inherit toolchain-scripts nopackages

do_populate_ide_support () {
  toolchain_create_tree_env_script
}

python () {
    sitefiles, searched = siteinfo_get_files(d, sysrootcache=False)
    d.setVar("CONFIG_SITE", " ".join(sitefiles))
    d.appendVarFlag("do_populate_ide_support", "file-checksums", " " + " ".join(searched))
}

addtask populate_ide_support before do_build after do_install
