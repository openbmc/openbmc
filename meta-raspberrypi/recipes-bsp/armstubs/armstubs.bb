DESCRIPTION = "Boot strap code that the GPU puts on memory to start running the boot loader"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://armstub.S;beginline=1;endline=26;md5=9888f34ac06a676129416c952a6a521e"

inherit deploy nopackages

include recipes-bsp/common/raspberrypi-tools.inc

COMPATIBLE_MACHINE = "^rpi$"

S = "${RPITOOLS_S}/armstubs"

export CC7 = "${CC}"
export LD7 = "${LD}"
export OBJCOPY7 = "${OBJCOPY}"
export OBJDUMP7 = "${OBJDUMP}"
export CC8 = "${CC}"
export LD8 = "${LD}"
export OBJCOPY8 = "${OBJCOPY}"
export OBJDUMP8 = "${OBJDUMP} -maarch64"

do_compile() {
    [ -z "${ARMSTUB}" ] && bbfatal "No ARMSTUB defined for your machine."
    oe_runmake ${ARMSTUB}
}

do_deploy() {
    install -d ${DEPLOYDIR}/${PN}
    cp ${S}/armstub*.bin ${DEPLOYDIR}/${PN}
}

addtask deploy before do_build after do_install
do_deploy[dirs] += "${DEPLOYDIR}/${PN}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

TOOLCHAIN = "gcc"
