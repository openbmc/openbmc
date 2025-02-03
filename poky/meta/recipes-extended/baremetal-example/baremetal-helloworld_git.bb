SUMMARY = "Baremetal examples to work with the several QEMU architectures supported on OpenEmbedded"
HOMEPAGE = "https://github.com/aehs29/baremetal-helloqemu"
DESCRIPTION = "These are introductory examples to showcase the use of QEMU to run baremetal applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39346640a23c701e4f459e05f56f4449"

SRCREV = "db2bf750eaef7fc0832e13ada8291343bbcc3afe"
PV = "0.1+git"

SRC_URI = "git://github.com/ahcbb6/baremetal-helloqemu.git;protocol=https;branch=master"
UPSTREAM_VERSION_UNKNOWN = "1"

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


# startup code for x86 uses NASM syntax
DEPENDS:qemux86:append = " nasm-native"

# These parameters are app specific for this example
# This will be translated automatically to the architecture and
# machine that QEMU uses on OE, e.g. -machine virt -cpu cortex-a57
# but the examples can also be run on other architectures/machines
# such as vexpress-a15 by overriding the setting on the machine.conf
COMPATIBLE_MACHINE = "qemuarmv5|qemuarm|qemuarm64|qemuriscv64|qemuriscv32|qemux86|qemux86-64"

BAREMETAL_QEMUARCH ?= ""
BAREMETAL_QEMUARCH:qemuarmv5 = "versatile"
BAREMETAL_QEMUARCH:qemuarm = "arm"
BAREMETAL_QEMUARCH:qemuarm64 = "aarch64"
BAREMETAL_QEMUARCH:qemuriscv64 = "riscv64"
BAREMETAL_QEMUARCH:qemuriscv32 = "riscv32"
BAREMETAL_QEMUARCH:qemux86 = "x86"
BAREMETAL_QEMUARCH:qemux86-64 = "x86-64"

EXTRA_OEMAKE:append = " QEMUARCH=${BAREMETAL_QEMUARCH} V=1"

# qemux86-64 uses a different Makefile
do_compile:prepend:qemux86-64(){
    cd x86-64
}

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

# qemux86-64 boots from iso rather than -kernel, create image to boot from
do_image:append:qemux86-64(){
    dd if=/dev/zero of=${B}/build/img.iso bs=1M count=10 status=none
    dd if=${B}/build/stage1.bin of=${B}/build/img.iso bs=512 count=1 conv=notrunc
    dd if=${B}/build/stage2.bin of=${B}/build/img.iso bs=512 seek=1 count=64 conv=notrunc
    dd if=${B}/build/hello_baremetal_x86-64.bin of=${B}/build/img.iso bs=512 seek=65 conv=notrunc
    install ${B}/build/img.iso ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.iso
}
