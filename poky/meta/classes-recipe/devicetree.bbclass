#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This bbclass implements device tree compilation for user provided device tree
# sources. The compilation of the device tree sources is the same as the kernel
# device tree compilation process, this includes being able to include sources
# from the kernel such as soc dtsi files or header files such as gpio.h. In
# addition to device trees this bbclass also handles compilation of device tree
# overlays.
#
# The output of this class behaves similar to how kernel-devicetree.bbclass
# operates in that the output files are installed into /boot/devicetree.
# However this class on purpose separates the deployed device trees into the
# 'devicetree' subdirectory. This prevents clashes with the kernel-devicetree
# output. Additionally the device trees are populated into the sysroot for
# access via the sysroot from within other recipes.

SECTION ?= "bsp"

# The default inclusion of kernel device tree includes and headers means that
# device trees built with them are at least GPL-2.0-only (and in some cases dual
# licensed). Default to GPL-2.0-only if the recipe does not specify a license.
LICENSE ?= "GPL-2.0-only"
LIC_FILES_CHKSUM ?= "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS += "dtc-native"

inherit deploy kernel-arch

COMPATIBLE_MACHINE ?= "^$"

PROVIDES = "virtual/dtb"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SYSROOT_DIRS += "/boot/devicetree"
FILES:${PN} = "/boot/devicetree/*.dtb /boot/devicetree/*.dtbo"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
B = "${WORKDIR}/build"

# Default kernel includes, these represent what are normally used for in-kernel
# sources.
KERNEL_INCLUDE ??= " \
        ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts \
        ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/* \
        ${STAGING_KERNEL_DIR}/scripts/dtc/include-prefixes \
        "

DT_INCLUDE[doc] = "Search paths to be made available to both the device tree compiler and preprocessor for inclusion."
DT_INCLUDE ?= "${DT_FILES_PATH} ${KERNEL_INCLUDE}"
DT_FILES_PATH[doc] = "Path to the directory containing dts files to build. Defaults to source directory."
DT_FILES_PATH ?= "${S}"
DT_FILES[doc] = "Space-separated list of dts or dtb files (relative to DT_FILES_PATH) to build. If empty, all dts files are built."
DT_FILES ?= ""

DT_PADDING_SIZE[doc] = "Size of padding on the device tree blob, used as extra space typically for additional properties during boot."
DT_PADDING_SIZE ??= "0x3000"
DT_RESERVED_MAP[doc] = "Number of reserved map entires."
DT_RESERVED_MAP ??= "8"
DT_BOOT_CPU[doc] = "The boot cpu, defaults to 0"
DT_BOOT_CPU ??= "0"

DTC_FLAGS ?= "-R ${DT_RESERVED_MAP} -b ${DT_BOOT_CPU}"
DTC_PPFLAGS ?= "-nostdinc -undef -D__DTS__ -x assembler-with-cpp"
DTC_BFLAGS ?= "-p ${DT_PADDING_SIZE} -@"
DTC_OFLAGS ?= "-p 0 -@ -H epapr"

python () {
    if d.getVar("KERNEL_INCLUDE"):
        # auto add dependency on kernel tree, but only if kernel include paths
        # are specified.
        d.appendVarFlag("do_compile", "depends", " virtual/kernel:do_configure")
}

def expand_includes(varname, d):
    import glob
    includes = set()
    # expand all includes with glob
    for i in (d.getVar(varname) or "").split():
        for g in glob.glob(i):
            if os.path.isdir(g): # only add directories to include path
                includes.add(g)
    return includes

def devicetree_source_is_overlay(path):
    # determine if a dts file is an overlay by checking if it uses "/plugin/;"
    with open(path, "r") as f:
        for i in f:
            if i.startswith("/plugin/;"):
                return True
    return False

def devicetree_compile(dtspath, includes, d):
    import subprocess
    dts = os.path.basename(dtspath)
    dtname = os.path.splitext(dts)[0]
    bb.note("Processing {0} [{1}]".format(dtname, dts))

    # preprocess
    ppargs = d.getVar("BUILD_CPP").split()
    ppargs += (d.getVar("DTC_PPFLAGS") or "").split()
    for i in includes:
        ppargs.append("-I{0}".format(i))
    ppargs += ["-o", "{0}.pp".format(dts), dtspath]
    bb.note("Running {0}".format(" ".join(ppargs)))
    subprocess.run(ppargs, check = True)

    # determine if the file is an overlay or not (using the preprocessed file)
    isoverlay = devicetree_source_is_overlay("{0}.pp".format(dts))

    # compile
    dtcargs = ["dtc"] + (d.getVar("DTC_FLAGS") or "").split()
    if isoverlay:
        dtcargs += (d.getVar("DTC_OFLAGS") or "").split()
    else:
        dtcargs += (d.getVar("DTC_BFLAGS") or "").split()
    for i in includes:
        dtcargs += ["-i", i]
    dtcargs += ["-o", "{0}.{1}".format(dtname, "dtbo" if isoverlay else "dtb")]
    dtcargs += ["-I", "dts", "-O", "dtb", "{0}.pp".format(dts)]
    bb.note("Running {0}".format(" ".join(dtcargs)))
    subprocess.run(dtcargs, check = True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

python devicetree_do_compile() {
    import re
    includes = expand_includes("DT_INCLUDE", d)
    dtfiles = d.getVar("DT_FILES").split()
    dtfiles = [ re.sub(r"\.dtbo?$", ".dts", dtfile) for dtfile in dtfiles ]
    listpath = d.getVar("DT_FILES_PATH")
    for dts in dtfiles or os.listdir(listpath):
        dtspath = os.path.join(listpath, dts)
        try:
            if not(os.path.isfile(dtspath)) or not(dts.endswith(".dts") or devicetree_source_is_overlay(dtspath)):
                continue # skip non-.dts files and non-overlay files
        except:
            continue # skip if can't determine if overlay
        devicetree_compile(dtspath, includes, d)
}

devicetree_do_install() {
    for DTB_FILE in `ls *.dtb *.dtbo`; do
        install -Dm 0644 ${B}/${DTB_FILE} ${D}/boot/devicetree/${DTB_FILE}
    done
}

devicetree_do_deploy() {
    for DTB_FILE in `ls *.dtb *.dtbo`; do
        install -Dm 0644 ${B}/${DTB_FILE} ${DEPLOYDIR}/devicetree/${DTB_FILE}
    done
}
addtask deploy before do_build after do_install

EXPORT_FUNCTIONS do_compile do_install do_deploy

