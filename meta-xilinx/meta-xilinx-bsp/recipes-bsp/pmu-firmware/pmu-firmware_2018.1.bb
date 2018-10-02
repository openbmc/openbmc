SUMMARY = "Firmware for the PMU on the ZynqMP Programmable Silicon"
HOMEPAGE = "https://github.com/Xilinx/embeddedsw"
SECTION = "bsp"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "virtual/${TARGET_PREFIX}gcc newlib libgloss libgcc"

# force this recipe to provide a target virtual/pmu-firmware. this is applied
# after any class extender mapping and results in this recipe always providing
# 'virtual/pmu-firmware'.
python append_target_provides () {
    d.appendVar("PROVIDES", " virtual/pmu-firmware")
}
addhandler append_target_provides
append_target_provides[eventmask] = "bb.event.RecipeParsed"

# This source links in a number of components with differing licenses, and some
# licenses are not Open Source compatible. Additionally the pmu-firmware source
# itself is licensed under a modified MIT license which restricts use to Xilinx
# devices only.
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://../../../../license.txt;md5=04841c5ad8409b474da7180de5391926"

inherit deploy

XILINX_RELEASE_VERSION = "v2018.1"
SRCREV = "aaa566bc3fa19255de4d434ebfa57ae3a9d261b2"
PV = "${XILINX_RELEASE_VERSION}+git${SRCPV}"

SRC_URI = "git://github.com/Xilinx/embeddedsw.git;protocol=https;nobranch=1"

COMPATIBLE_HOST = "microblaze.*-elf"
COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynqmp = "zynqmp"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git/lib/sw_apps/zynqmp_pmufw/src"

# The makefile does not handle parallelization
PARALLEL_MAKE = ""

do_configure() {
	# manually do the copy_bsp step first, so as to be able to fix up use of
	# mb-* commands
	${S}/../misc/copy_bsp.sh
}

COMPILER = "${CC}"
COMPILER_FLAGS = "-O2 -c"
EXTRA_COMPILER_FLAGS = "-g -Wall -Wextra -Os -flto -ffat-lto-objects"
ARCHIVER = "${AR}"

# HACK: fix the dirty bug where xilsecure wants to call this PSVersion
# function, which is not implemented for microblaze. The symbols never make it
# into the final elf as the xilsecure function that uses it is not called in
# pmufw.
EXTRA_COMPILER_FLAGS_append = " -DXGetPSVersion_Info=atexit"

BSP_DIR ?= "${S}/../misc/zynqmp_pmufw_bsp"
BSP_TARGETS_DIR ?= "${BSP_DIR}/psu_pmu_0/libsrc"

def bsp_make_vars(d):
    s = ["COMPILER", "CC", "COMPILER_FLAGS", "EXTRA_COMPILER_FLAGS", "ARCHIVER", "AR", "AS"]
    return " ".join(["\"%s=%s\"" % (v, d.getVar(v)) for v in s])

do_compile() {
	# the Makefile in ${S}/../misc/Makefile, does not handle CC, AR, AS, etc
	# properly. So do its job manually. Preparing the includes first, then libs.
	for i in $(ls ${BSP_TARGETS_DIR}/*/src/Makefile); do
		oe_runmake -C $(dirname $i) -s include ${@bsp_make_vars(d)}
	done
	for i in $(ls ${BSP_TARGETS_DIR}/*/src/Makefile); do
		oe_runmake -C $(dirname $i) -s libs ${@bsp_make_vars(d)}
	done

	# --build-id=none is required due to linker script not defining a location for it.
	oe_runmake CC="${CC}" CC_FLAGS="-MMD -MP -Wl,--build-id=none"
}

do_install() {
	:
}

PMU_FIRMWARE_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
PMU_FIRMWARE_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {
	install -Dm 0644 ${B}/executable.elf ${DEPLOYDIR}/${PMU_FIRMWARE_BASE_NAME}.elf
	ln -sf ${PMU_FIRMWARE_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf
	ln -sf ${BPN}-${MACHINE}.elf ${DEPLOYDIR}/pmu-${MACHINE}.elf
	${OBJCOPY} -O binary ${B}/executable.elf ${B}/executable.bin
	install -m 0644 ${B}/executable.bin ${DEPLOYDIR}/${PMU_FIRMWARE_BASE_NAME}.bin
	ln -sf ${PMU_FIRMWARE_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
	ln -sf ${BPN}-${MACHINE}.bin ${DEPLOYDIR}/pmu-${MACHINE}.bin
}
addtask deploy before do_build after do_install

BBCLASSEXTEND = "zynqmp-pmu"
