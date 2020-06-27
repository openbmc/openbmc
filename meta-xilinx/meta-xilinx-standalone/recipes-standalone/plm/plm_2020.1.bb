inherit deploy

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://../../../../license.txt;md5=39ab6ab638f4d1836ba994ec6852de94"

SRCREV = "e8db5fb118229fdc621e0ec7848641a23bf60998"
PV = "${XILINX_RELEASE_VERSION}+git${SRCPV}"

SRC_URI = "git://github.com/Xilinx/embeddedsw.git;protocol=https;nobranch=1"

COMPATIBLE_HOST = "microblaze.*-elf"
COMPATIBLE_MACHINE = "versal-mb"


S = "${WORKDIR}/git/lib/sw_apps/versal_plm/src"

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

BSP_DIR ?= "${S}/../misc/versal_plm_bsp"
BSP_TARGETS_DIR ?= "${BSP_DIR}/psv_pmc_0/libsrc"

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
    # Again, recipe-systoot include is necessary
    oe_runmake CC="${CC}" CC_FLAGS="-MMD -MP -Wl,--build-id=none -I${STAGING_DIR_TARGET}/usr/include"
}

do_install() {
    :
}

PLM_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
PLM_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {
    install -Dm 0644 ${B}/plm.elf ${DEPLOYDIR}/${PLM_BASE_NAME}.elf
    ln -sf ${PLM_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf
    ${OBJCOPY} -O binary ${B}/plm.elf ${B}/plm.bin
    install -m 0644 ${B}/plm.bin ${DEPLOYDIR}/${PLM_BASE_NAME}.bin
    ln -sf ${PLM_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
}

addtask deploy before do_build after do_install

