DESCRIPTION = "A simple memory benchmark program, which tries to measure the \
peak bandwidth of sequential memory accesses and the latency of random memory \
accesses. Bandwidth is measured by running different assembly code for the \
aligned memory blocks and attempting different prefetch strategies"
HOMEPAGE = "https://github.com/ssvb/tinymembench/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://main.c;endline=22;md5=879b9bbb60851454885b5fa47eb6b345"

PV = "0.4.0+git${SRCPV}"

SRCREV = "a2cf6d7e382e3aea1eb39173174d9fa28cad15f3"
SRC_URI = "git://github.com/ssvb/tinymembench.git;branch=master;protocol=https \
           file://0001-asm-Delete-.func-.endfunc-directives.patch \
           "

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    install -d ${D}${bindir}
    install -m755 tinymembench ${D}${bindir}/
}

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:66: Error: instruction not supported in Thumb16 mode -- `subs r1,r1,#16'
#| {standard input}:69: Error: instruction not supported in Thumb16 mode -- `subs r1,r1,#16'
#| {standard input}:82: Error: selected processor does not support Thumb mode `mla r3,r4,r3,r5'
#| {standard input}:82: Error: unshifted register required -- `and r8,r7,r3,lsr#16'
ARM_INSTRUCTION_SET = "arm"
#
# Does not work for 64bit mips.
#
COMPATIBLE_HOST = "^(?!mips64).*"
