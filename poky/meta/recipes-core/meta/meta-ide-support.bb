SUMMARY = "Integrated Development Environment support"
DESCRIPTION = "Meta package for ensuring the build directory contains all appropriate toolchain packages for using an IDE"
LICENSE = "MIT"

DEPENDS = "virtual/libc gdb-cross-${TARGET_ARCH} qemu-native qemu-helper-native unfs3-native cmake-native autoconf-native automake-native meson-native intltool-native pkgconfig-native"
RM_WORK_EXCLUDE += "${PN}"

inherit toolchain-scripts nopackages deploy testsdk

TESTSDK_CLASS_NAME = "oeqa.sdk.testmetaidesupport.TestSDK"

do_populate_ide_support () {
  toolchain_create_tree_env_script
}

python () {
    sitefiles, searched = siteinfo_get_files(d, sysrootcache=False)
    d.setVar("CONFIG_SITE", " ".join(sitefiles))
    d.appendVarFlag("do_populate_ide_support", "file-checksums", " " + " ".join(searched))
}

addtask populate_ide_support before do_deploy after do_install

python do_write_test_data() {
    from oe.data import export2json

    out_dir = d.getVar('B')
    testdata_name = os.path.join(out_dir, "%s.testdata.json" % d.getVar('PN'))

    export2json(d, testdata_name)
}
addtask write_test_data before do_deploy after do_install

do_deploy () {
        install ${B}/* ${DEPLOYDIR}
}

addtask deploy before do_build

do_build[deptask] += "do_prepare_recipe_sysroot"
