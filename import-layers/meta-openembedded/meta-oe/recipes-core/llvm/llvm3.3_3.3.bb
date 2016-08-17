require llvm.inc

DEPENDS += "zlib"
EXTRA_OECONF += "--enable-zlib"

SRC_URI += "file://Remove-error-output-from-configure-if-CFLAGS-is-set-.patch"

SRC_URI_append_libc-uclibc = " file://arm_fenv_uclibc.patch "

SRC_URI[md5sum] = "40564e1dc390f9844f1711c08b08e391"
SRC_URI[sha256sum] = "68766b1e70d05a25e2f502e997a3cb3937187a3296595cf6e0977d5cd6727578"

PACKAGECONFIG ??= ""
PACKAGECONFIG[r600] = "--enable-experimental-targets=R600,,,"

# Fails to build with thumb-1 (qemuarm)
# | {standard input}: Assembler messages:
# | {standard input}:22: Error: selected processor does not support Thumb mode `stmdb sp!,{r0,r1,r2,r3,lr}'
# | {standard input}:31: Error: lo register required -- `ldmia sp!,{r0,r1,r2,r3,lr}'
# | {standard input}:32: Error: lo register required -- `ldr pc,[sp],#4'
# | make[3]: *** [/home/jenkins/oe/world/shr-core/tmp-glibc/work/armv5te-oe-linux-gnueabi/llvm3.3/3.3-r0/llvm-3.3.build/lib/Target/ARM/Release/ARMJITInfo.o] Error 1
ARM_INSTRUCTION_SET = "arm"
