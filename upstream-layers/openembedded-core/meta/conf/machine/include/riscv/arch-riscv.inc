# RISCV Architecture definition

# Based on the RISC-V Instruction Set Manual Volume I: Unprivileged ISA from May 2025
# As well as the RISC-V options for using GCC (as of June 2025)

# Note: the following should be implemented in the order that GCC expects
# -march= values to be defined in.

# Base ISA
# All supported march strings must start with rv32 or rv64
TUNEVALID[rv] = "RISC-V"
TUNE_RISCV_ARCH = "${@bb.utils.contains("TUNE_FEATURES", "rv", "riscv", "", d)}"
TUNE_RISCV_MARCH = "${@bb.utils.contains("TUNE_FEATURES", "rv", "rv", "", d)}"
TUNE_RISCV_ABI = ""

# There are two primary ABIs, ilp32 and lp64
# There are variants of both, that appears to be based on extensions above
# For example:
#   rv32i uses ilp32, rv32e uses ilp32e, rv32f uses ilp32f
#   rv64i uses lp64, rv64if uses lp64f, rv64id uses lp64d
TUNEVALID[32] = "ISA XLEN - 32-bit"
TUNECONFLICTS[32] = "64"
TUNE_RISCV_ARCH .= "${@bb.utils.contains("TUNE_FEATURES", "32", "32", "", d)}"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "32", "32", "", d)}"
TUNE_RISCV_ABI .= "${@bb.utils.contains("TUNE_FEATURES", "32", "ilp32", "", d)}"

TUNEVALID[64] = "ISA XLEN - 64-bit"
TUNECONFLICTS[64] = "32"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "64", "64", "", d)}"
TUNE_RISCV_ARCH .= "${@bb.utils.contains("TUNE_FEATURES", "64", "64", "", d)}"
TUNE_RISCV_ABI .= "${@bb.utils.contains("TUNE_FEATURES", "64", "lp64", "", d)}"

# The package arch starts with the canonical arch, but adds some extensions to make
# package compatibility clear
TUNE_RISCV_PKGARCH = "${TUNE_RISCV_ARCH}"

# i, e, or g are defined by gcc, but 'g' refers to 'i' + extensions 'MAFD Zicsr Zifencei'
# So 'g' will not be defined here as it is an abbreviation of the expanded version
TUNEVALID[e] = "Reduced register base integer extension"
TUNECONFLICTS[e] = "i"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "e", "e", "", d)}"
TUNE_RISCV_ABI .= "${@bb.utils.contains("TUNE_FEATURES", "e", "e", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "e", "e", "", d)}"

TUNEVALID[i] = "Base integer extension"
TUNECONFLICTS[i] = "e"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "i", "i", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "i", "i", "", d)}"

# Extensions
TUNEVALID[m] = "Integer multiplication and division extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "m", "m", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "m", "m", "", d)}"

TUNEVALID[a] = "Atomic extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "a", "a", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "a", "a", "", d)}"

TUNEVALID[f] = "Single-precision floating-point extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "f d", "f", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "f d", "f", "", d)}"

TUNEVALID[d] = "Double-precision floating-point extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "d", "d", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "d", "d", "", d)}"

# Only f OR d, but just one
TUNE_RISCV_ABI .= "${@bb.utils.contains("TUNE_FEATURES", "d", "d", bb.utils.contains("TUNE_FEATURES", "f", "f", "", d), d)}"

TUNEVALID[c] = "Compressed extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "c", "c", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "c", "c", "", d)}"

TUNEVALID[b] = "Bit Manipulation extension"
# Handled below via zba, zbb, zbs
# This matches current Linux kernel behavior
#TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "b", "b", "", d)}"
#TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "b", "b", "", d)}"

TUNEVALID[v] = "Vector operations extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "v", "v", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "v", "v", "", d)}"

# Now the special Z extensions
TUNEVALID[zicbom] = "Cache-block management extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "zicbom", "_zicbom", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "zicbom", "_zicbom", "", d)}"

TUNEVALID[zicsr] = "Control and status register access extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "zicsr f d", "_zicsr", "", d)}"
# If zicsr (or zifencei) is in the path, OpenSBI fails to use the extensions, do to (Makefile):
#   # Check whether the assembler and the compiler support the Zicsr and Zifencei extensions
#   CC_SUPPORT_ZICSR_ZIFENCEI := $(shell $(CC) $(CLANG_TARGET) $(RELAX_FLAG) -nostdlib -march=rv$(OPENSBI_CC_XLEN)imafd_zicsr_zifencei -x c /dev/null -o /dev/null 2>&1 | grep -e "zicsr" -e "zifencei" > /dev/null && echo n || echo y)
# this will match on the path containing zicsr or zifencei when an error is reported, which
# will always happens in this check.
#
# Yocto Project Bugzilla 15897
#
#TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "zicsr f d", "_zicsr", "", d)}"

TUNEVALID[zifencei] = "Instruction-fetch fence extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains("TUNE_FEATURES", "zifencei", "_zifencei", "", d)}"
# See above Bug 15897
#TUNE_RISCV_PKGARCH .= "${@bb.utils.contains("TUNE_FEATURES", "zifencei", "_zifencei", "", d)}"

TUNEVALID[zba] = "Address bit manipulation extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "b zba", "_zba", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "b zba", "_zba", "", d)}"

TUNEVALID[zbb] = "Basic bit manipulation extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "b zbb", "_zbb", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "b zbb", "_zbb", "", d)}"

TUNEVALID[zbc] = "Carry-less multiplication extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "zbc", "_zbc", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "zbc", "_zbc", "", d)}"

TUNEVALID[zbs] = "Single-bit manipulation extension"
TUNE_RISCV_MARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "b zbs", "_zbs", "", d)}"
TUNE_RISCV_PKGARCH .= "${@bb.utils.contains_any("TUNE_FEATURES", "b zbs", "_zbs", "", d)}"

# Construct TUNE_CCARGS
# This should result in a CCARG similar to:
#   -march=rv32imac -mabi=ilp32
TUNE_CCARGS = "${@ '-march=${TUNE_RISCV_MARCH} -mabi=${TUNE_RISCV_ABI}' if not d.getVar('TUNE_CCARGS:tune-${DEFAULTTUNE}') else 'TUNE_CCARGS:tune-${DEFAULTTUNE}'}"

# Construct TUNE_ARCH
# This should result in an arch string similar to:
#   riscv32
TUNE_ARCH = "${TUNE_RISCV_ARCH}"

# Construct TUNE_PKGARCH
# This should result in a package are like:
#    riscv32imac
TUNE_PKGARCH = "${TUNE_RISCV_PKGARCH}"

# Misc settings
# Fix: ld: unrecognized option '--hash-style=sysv'
LINKER_HASH_STYLE:libc-newlib = ""
LINKER_HASH_STYLE:libc-picolibc = ""
# Fix: ld: unrecognized option '--hash-style=gnu'
LINKER_HASH_STYLE:libc-baremetal = ""
