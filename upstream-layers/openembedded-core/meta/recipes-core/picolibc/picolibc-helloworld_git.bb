require picolibc.inc

# baremetal-image overrides
BAREMETAL_BINNAME ?= "hello_picolibc_${MACHINE}"
IMAGE_LINK_NAME ?= "baremetal-picolibc-image-${MACHINE}"
IMAGE_NAME_SUFFIX ?= ""
QB_DEFAULT_KERNEL ?= "${IMAGE_LINK_NAME}.elf"

inherit baremetal-image

COMPATIBLE_MACHINE = "qemuarm|qemuarm64|qemuriscv32|qemuriscv64"

# Use semihosting to test via QEMU
QB_OPT_APPEND:append = " -semihosting-config enable=on"

# picolibc comes with a set of linker scripts, set the file
# according to the architecture being built.
PICOLIBC_LINKERSCRIPT:qemuarm64 = "aarch64.ld"
PICOLIBC_LINKERSCRIPT:qemuarm = "arm.ld"
PICOLIBC_LINKERSCRIPT:qemuriscv32 = "riscv.ld"
PICOLIBC_LINKERSCRIPT:qemuriscv64 = "riscv.ld"

# Simple compile function that manually exemplifies usage; as noted,
# use a custom linker script, the GCC specs provided by picolibc
# and semihost to be able to test via QEMU's monitor
do_compile(){
    ${CC} ${CFLAGS} ${LDFLAGS} --verbose -T${S}/hello-world/${PICOLIBC_LINKERSCRIPT} -specs=picolibc.specs --oslib=semihost -o ${BAREMETAL_BINNAME}.elf ${S}/hello-world/hello-world.c
    ${OBJCOPY} -O binary ${BAREMETAL_BINNAME}.elf ${BAREMETAL_BINNAME}.bin
}

do_install(){
    install -d ${D}/${base_libdir}/firmware
    install -m 755 ${B}/${BAREMETAL_BINNAME}.elf ${D}/${base_libdir}/firmware/${BAREMETAL_BINNAME}.elf
    install -m 755 ${B}/${BAREMETAL_BINNAME}.bin ${D}/${base_libdir}/firmware/${BAREMETAL_BINNAME}.bin
}

FILES:${PN} += " \
    ${base_libdir}/firmware/${BAREMETAL_BINNAME}.elf \
    ${base_libdir}/firmware/${BAREMETAL_BINNAME}.bin \
"
