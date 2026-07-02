deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot
deltask do_populate_lic
RM_WORK_EXCLUDE += "${PN}"

inherit nopackages allarch

PN = "gcc-source-${PV}"
WORKDIR = "${TMPDIR}/work-shared/gcc-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:gcc::${PV}:${PR}::${SSTATE_VERSION}:"

STAMP = "${STAMPS_DIR}/work-shared/gcc-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/gcc-${PV}-*"

DEPENDS = ""
PACKAGES = ""
baselib = "lib"
PACKAGE_ARCH = "all"

B = "${WORKDIR}/build"

# This needs to be Python to avoid lots of shell variables becoming dependencies.
python do_preconfigure () {
    import subprocess
    source = d.getVar("S")
    subprocess.check_output(['gnu-configize'], stderr=subprocess.STDOUT, cwd=source)
    cmd = ['sed', '-i', '-e', 's/BUILD_INFO=info/BUILD_INFO=/', source + '/gcc/configure']
    subprocess.check_output(cmd)

    # Easiest way to stop bad RPATHs getting into the library since we have a
    # broken libtool here (breaks cross-canadian and target at least)
    cmd = ['sed', '-i', '-e', 's/hardcode_into_libs=yes/hardcode_into_libs=no/', source + '/libcc1/configure']
    subprocess.check_output(cmd)
}
addtask do_preconfigure after do_patch
do_preconfigure[depends] += "gnu-config-native:do_populate_sysroot autoconf-native:do_populate_sysroot"

