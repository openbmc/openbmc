INHIBIT_DEFAULT_DEPS = "1"
LICENSE = "MIT"

STANDALONE_SYSROOT = "${STAGING_DIR}/${MACHINE}"
STANDALONE_SYSROOT_NATIVE = "${STAGING_DIR}/${BUILD_ARCH}"
PACKAGE_ARCH = "${MACHINE_ARCH}"
EXCLUDE_FROM_WORLD = "1"

inherit nopackages
deltask fetch
deltask unpack
deltask patch
deltask prepare_recipe_sysroot
deltask populate_lic
deltask configure
deltask compile
deltask install
deltask populate_sysroot

python do_build_native_sysroot () {
    targetsysroot = d.getVar("STANDALONE_SYSROOT")
    nativesysroot = d.getVar("STANDALONE_SYSROOT_NATIVE")
    import os
    os.environ['PATH'] = "%s/bin:%s/usr/bin:%s" % (nativesysroot, nativesysroot, os.environ['PATH'])
    staging_populate_sysroot_dir(targetsysroot, nativesysroot, True, d)
}
do_build_native_sysroot[cleandirs] = "${STANDALONE_SYSROOT_NATIVE}"
do_build_native_sysroot[nostamp] = "1"
addtask do_build_native_sysroot before do_build

python do_build_target_sysroot () {
    targetsysroot = d.getVar("STANDALONE_SYSROOT")
    nativesysroot = d.getVar("STANDALONE_SYSROOT_NATIVE")
    import os
    os.environ['PATH'] = "%s/bin:%s/usr/bin:%s" % (nativesysroot, nativesysroot, os.environ['PATH'])
    staging_populate_sysroot_dir(targetsysroot, nativesysroot, False, d)
}
do_build_target_sysroot[cleandirs] = "${STANDALONE_SYSROOT}"
do_build_target_sysroot[nostamp] = "1"
addtask do_build_target_sysroot before do_build

do_clean[cleandirs] += "${STANDALONE_SYSROOT} ${STANDALONE_SYSROOT_NATIVE}"
