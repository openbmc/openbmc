SUMMARY = "Baremetal examples to work with the several QEMU architectures supported on OpenEmbedded"
HOMEPAGE = "https://github.com/aehs29/baremetal-helloqemu"
DESCRIPTION = "These are introductory examples to showcase the use of QEMU to run baremetal applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39346640a23c701e4f459e05f56f4449"

SRCREV = "31b4e5a337018b4a00a7426b0e5ed83b81df30c7"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/aehs29/baremetal-helloqemu.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

# The following variables should be set to accomodate each application
BAREMETAL_BINNAME ?= "hello_baremetal_${MACHINE}"
IMAGE_LINK_NAME ?= "baremetal-helloworld-image-${MACHINE}"
IMAGE_NAME_SUFFIX ?= ""

# Baremetal-Image creates the proper wiring, assumes the output is provided in
# binary and ELF format, installed on ${base_libdir}/firmware/ , we want a
# package to be created since we might have some way of updating the baremetal
# firmware from Linux
inherit baremetal-image


# These parameters are app specific for this example
# This will be translated automatically to the architecture and
# machine that QEMU uses on OE, e.g. -machine virt -cpu cortex-a57
# but the examples can also be run on other architectures/machines
# such as vexpress-a15 by overriding the setting on the machine.conf
COMPATIBLE_MACHINE = "qemuarmv5|qemuarm|qemuarm64|qemuriscv64|qemuriscv32"

BAREMETAL_QEMUARCH ?= ""
BAREMETAL_QEMUARCH:qemuarmv5 = "versatile"
BAREMETAL_QEMUARCH:qemuarm = "arm"
BAREMETAL_QEMUARCH:qemuarm64 = "aarch64"
BAREMETAL_QEMUARCH:qemuriscv64 = "riscv64"
BAREMETAL_QEMUARCH:qemuriscv32 = "riscv32"

EXTRA_OEMAKE:append = " QEMUARCH=${BAREMETAL_QEMUARCH} V=1"


# Install binaries on the proper location for baremetal-image to fetch and deploy
do_install(){
    install -d ${D}/${base_libdir}/firmware
    install -m 755 ${B}/build/hello_baremetal_${BAREMETAL_QEMUARCH}.bin ${D}/${base_libdir}/firmware/${BAREMETAL_BINNAME}.bin
    install -m 755 ${B}/build/hello_baremetal_${BAREMETAL_QEMUARCH}.elf ${D}/${base_libdir}/firmware/${BAREMETAL_BINNAME}.elf
}

FILES:${PN} += " \
    ${base_libdir}/firmware/${BAREMETAL_BINNAME}.bin \
    ${base_libdir}/firmware/${BAREMETAL_BINNAME}.elf \
"
