deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot
deltask do_populate_lic
RM_WORK_EXCLUDE += "${PN}"

inherit nopackages

PN = "llvm-project-source-${PV}"
WORKDIR = "${TMPDIR}/work-shared/llvm-project-source-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:llvm-project-source::${PV}:${PR}::${SSTATE_VERSION}:"

STAMP = "${STAMPS_DIR}/work-shared/llvm-project-source-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/llvm-project-source-${PV}-*"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = ""
PACKAGES = ""
TARGET_ARCH = "allarch"
TARGET_AS_ARCH = "none"
TARGET_CC_ARCH = "none"
TARGET_LD_ARCH = "none"
TARGET_OS = "linux"
baselib = "lib"
PACKAGE_ARCH = "all"

B = "${WORKDIR}/build"

# space separated list of additional distro vendor values we want to support e.g.
# "yoe webos" or "-yoe -webos" '-' is optional
CLANG_EXTRA_OE_VENDORS ?= "${TARGET_VENDOR} ${SDK_VENDOR}"
# Extra OE DISTRO that want to support as build host. space separated list of additional distro.
# ":" separated the ID in "/etc/os-release" and the triple for finding gcc on this OE DISTRO.
# eg: "poky:poky wrlinux:wrs"
CLANG_EXTRA_OE_DISTRO ?= "poky:poky"
# Match with MULTILIB_GLOBAL_VARIANTS
ML_VARIANTS = "lib32 lib64 libx32"

python do_preconfigure() {
    import subprocess
    case = ""
    triple = ""
    vendors = d.getVar('CLANG_EXTRA_OE_VENDORS')
    multilib_variants = (d.getVar("ML_VARIANTS") or "").split()
    vendors_to_add = []
    for vendor in vendors.split():
        # convert -yoe into yoe
        vendor = vendor.lstrip('-')
        # generate possible multilib vendor names for yoe
        # such as yoemllib32
        vendors_to_add.extend([vendor + 'ml' + variant for variant in multilib_variants])
        # skip oe since already part of the cpp file
        if vendor != "oe":
            vendors_to_add.append(vendor)

    for vendor_to_add in vendors_to_add:
        case += '\\n    .Case("' + vendor_to_add + '", Triple::OpenEmbedded)'
        triple += ' "x86_64-' + vendor_to_add + '-linux",'

    bb.note("Adding support following TARGET_VENDOR values")
    bb.note(str(vendors_to_add))
    bb.note("in llvm/lib/TargetParser/Triple.cpp and ${S}/clang/lib/Driver/ToolChains/Gnu.cpp")
    cmd = d.expand("sed -i 's#//CLANG_EXTRA_OE_VENDORS_TRIPLES#%s#g' ${S}/clang/lib/Driver/ToolChains/Gnu.cpp" % (triple))
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    cmd = d.expand("sed -i 's#//CLANG_EXTRA_OE_VENDORS_CASES#%s#g' -i ${S}/llvm/lib/TargetParser/Triple.cpp" % (case))
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)

    case = ""
    triple = ""
    name = ""
    check = ""
    oe_names = ""
    distros = d.getVar('CLANG_EXTRA_OE_DISTRO')
    for distro in distros.split():
        distro_id = distro.split(":")[0].replace('-','_')
        distro_triple = distro.split(":")[1]
        case += '\\n    .Case("' + distro_id + '", Distro::' + distro_id.upper() + ')'
        triple += '\\n   if (Distro.Is' + distro_id.upper() + '())\\n     return "x86_64-' + distro_triple + '-linux",'
        name += '\\n    '+ distro_id.upper() + ','
        check += '\\nbool Is' + distro_id.upper() + '() const { return DistroVal == ' + distro_id.upper() + '; }'
        oe_names +=  distro_id.upper() + ' ||'

    check += '\\nbool IsOpenEmbedded() const { return DistroVal == ' + oe_names[0:-3] + '; }'

    cmd = d.expand("sed -i 's#//CLANG_EXTRA_OE_DISTRO_NAME#%s#g' ${S}/clang/include/clang/Driver/Distro.h" % (name))
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    cmd = d.expand("sed -i 's#//CLANG_EXTRA_OE_DISTRO_CHECK#%s#g' ${S}/clang/include/clang/Driver/Distro.h" % (check))
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    cmd = d.expand("sed -i 's#//CLANG_EXTRA_OE_DISTRO_TRIPLES#%s#g' ${S}/clang/lib/Driver/ToolChains/Linux.cpp" % (triple))
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    cmd = d.expand("sed -i 's#//CLANG_EXTRA_OE_DISTRO_CASES#%s#g' -i ${S}/clang/lib/Driver/Distro.cpp" % (case))
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}

do_patch[vardepsexclude] += "MULTILIBS MULTILIB_VARIANTS"
addtask do_preconfigure after do_patch
do_create_spdx[depends] += "${PN}:do_preconfigure"
