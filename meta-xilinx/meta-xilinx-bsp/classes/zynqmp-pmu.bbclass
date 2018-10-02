#
# This class handles configuring a recipe to build for the ZynqMP PMU
# architecture. The reason for this class is due to limitations of multilib
# with regards to multiple architectures (which do not work correctly).
#
# This class is specifically intended to extend the binutils-cross, gcc-cross,
# newlib, libgloss and pmu-firmware recipes so that binaries can be emitted
# which target the PMU architecture alongside building for the APU architecture
# (ARM64). But the class can be applied globally via BBCLASSEXTEND in for
# example a <machine>.conf.
#
# This class is almost the same as a multilib variant with custom TUNE_* setup
# to allow for a switched TUNE_ARCH.
#

ORIG_TARGET_ARCH := "${TARGET_ARCH}"

# zynqmp-pmu target arch (hardcoded based on pre-gen data from arch-microblaze.inc)
DEFAULTTUNE = "microblaze"
ABIEXTENSION = ""
TUNE_ARCH = "microblazeel"
#TUNE_FEATURES_tune-microblaze += "v9.2 barrel-shift pattern-compare"
TUNE_CCARGS = "-mlittle-endian -mxl-barrel-shift -mxl-pattern-compare -mno-xl-reorder -mcpu=v9.2 -mxl-soft-mul -mxl-soft-div"
TUNE_LDARGS = ""
TUNE_ASARGS = ""
TUNE_PKGARCH = "microblazeel-v9.2-bs-cmp"
TARGET_OS = "elf"
TARGET_FPU = "fpu-soft"

# rebuild the MACHINE overrides
MACHINEOVERRIDES = "${MACHINE}${@':${SOC_FAMILY}' if d.getVar('SOC_FAMILY') else ''}:microblaze"

# override tune provided archs
PACKAGE_EXTRA_ARCHS = "${TUNE_PKGARCH}"

# baremetal equivalent config (note the tclibc is not included, this is purely
# for recipes/etc that check for the value)
TCLIBC = "baremetal"
LIBCEXTENSION = ""
LIBCOVERRIDE = ":libc-baremetal"
USE_NLS = "no"
IMAGE_LINGUAS = ""
LIBC_DEPENDENCIES = ""

# gcc-cross specific baremetal setup (due to the override order this is important)
EXTRA_OECONF_pn-${MLPREFIX}gcc-cross-${TARGET_ARCH}_append = " --without-headers"

EXTRA_OECONF_GCC_FLOAT = ""

# Setup a multiarch like prefix.
prefix = "/usr/${TARGET_SYS}"
# Make sure GCC can search in the prefix dir (for libgcc)
TOOLCHAIN_OPTIONS += "-B${RECIPE_SYSROOT}${includedir}/ -B${RECIPE_SYSROOT}${libdir}/"
TOOLCHAIN_OPTIONS += "-I =${includedir} -L =${libdir}"

python multitarget_zynqmp_pmu_virtclass_handler () {
    variant = "zynqmp-pmu"
    pn = d.getVar("PN")
    if not (pn.startswith(variant + "-") or pn.endswith("-" + variant)):
        return

    if bb.data.inherits_class('native', e.data) or bb.data.inherits_class('nativesdk', e.data) or bb.data.inherits_class('crosssdk', e.data):
        raise bb.parse.SkipPackage("Can't extend native/nativesdk/crosssdk recipes")

    initialpn = e.data.getVar("PN").replace("-" + variant, "").replace(variant + "-", "")
    e.data.setVar("MLPREFIX", variant + "-")
    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + ":virtclass-" + variant)

    # hide multilib variants, this class is not one but this works around recipes thinking it is (due to MLPREFIX).
    e.data.setVar("MULTILIB_VARIANTS", "")

    # work around for -cross recipes that embed the TARGET_ARCH value
    if bb.data.inherits_class('cross', e.data):
        if initialpn.endswith("-" + d.getVar("ORIG_TARGET_ARCH")):
            initialpn = initialpn.replace("-" + d.getVar("ORIG_TARGET_ARCH"), "-" + d.getVar("TARGET_ARCH"))

    e.data.setVar("PN", variant + "-" + initialpn)
}

addhandler multitarget_zynqmp_pmu_virtclass_handler
multitarget_zynqmp_pmu_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

python () {
    variant = "zynqmp-pmu"
    pn = d.getVar("PN")
    if not pn.startswith(variant + "-"):
        return

    if pn.endswith("gcc-cross-" + d.getVar("TARGET_ARCH")):
        # work around, DEPENDS _remove being immediate in gcc-cross
        d.setVar("DEPENDS_remove", "virtual/%slibc-for-gcc" % d.getVar("TARGET_PREFIX"))

    if pn.endswith("libgcc"):
        # work around, strip depends on libc via do_package* tasks (this class cannot set ASSUME_PROVIDED += libc)
        for i in ["do_package", "do_package_write_ipk", "do_package_write_deb", "do_package_write_rpm"]:
            sanitized = " ".join([dep for dep in d.getVarFlag(i, "depends").split() if not dep.startswith("virtual/%s-libc" % variant)])
            d.setVarFlag(i, "depends", sanitized)

    import oe.classextend

    clsextend = oe.classextend.ClassExtender(variant, d)

    clsextend.map_depends_variable("DEPENDS")
    clsextend.map_variable("PROVIDES")

    clsextend.rename_packages()
    clsextend.rename_package_variables((d.getVar("PACKAGEVARS") or "").split())

    clsextend.map_packagevars()
    clsextend.map_regexp_variable("PACKAGES_DYNAMIC")
    clsextend.map_variable("PACKAGE_INSTALL")
}

# microblaze elf insane definitions not currently in insane.bbclass
PACKAGEQA_EXTRA_MACHDEFFUNCS += "package_qa_get_machine_dict_microblazeelf"
def package_qa_get_machine_dict_microblazeelf(machdata, d):
    machdata["elf"] =  {
                        "microblaze":  (189,   0,    0,          False,         32),
                        "microblazeeb":(189,   0,    0,          False,         32),
                        "microblazeel":(189,   0,    0,          True,          32),
                      }
    return machdata
