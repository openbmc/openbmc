deltask do_configure 
deltask do_compile 
deltask do_install 
deltask do_populate_sysroot
deltask do_populate_lic 
RM_WORK_EXCLUDE += "${PN}"

inherit nopackages

PN = "gcc-source-${PV}"
WORKDIR = "${TMPDIR}/work-shared/gcc-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:gcc::${PV}:${PR}::${SSTATE_VERSION}:"

STAMP = "${STAMPS_DIR}/work-shared/gcc-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/gcc-${PV}-*"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = ""
PACKAGES = ""


# This needs to be Python to avoid lots of shell variables becoming dependencies.
python do_preconfigure () {
    import subprocess
    cmd = d.expand('cd ${S} && PATH=${PATH} gnu-configize')
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    # See 0044-gengtypes.patch, we need to regenerate this file
    bb.utils.remove(d.expand("${S}/gcc/gengtype-lex.c"))
    cmd = d.expand("sed -i 's/BUILD_INFO=info/BUILD_INFO=/' ${S}/gcc/configure")
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)

    # Easiest way to stop bad RPATHs getting into the library since we have a
    # broken libtool here (breaks cross-canadian and target at least)
    cmd = d.expand("sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${S}/libcc1/configure")
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}
addtask do_preconfigure after do_patch
do_preconfigure[depends] += "gnu-config-native:do_populate_sysroot autoconf-native:do_populate_sysroot"

