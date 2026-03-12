LLVM_RELEASE = ""
LLVM_DIR = "llvm${LLVM_RELEASE}"

LLVM_HTTP ?= "https://github.com/llvm"

MAJOR_VER = "21"
MINOR_VER = "1"
PATCH_VER = "8"
# could be 'rcX' or 'git' or empty ( for release )
VER_SUFFIX = ""

PV = "${MAJOR_VER}.${MINOR_VER}.${PATCH_VER}"

LLVMMD5SUM = "8a15a0759ef07f2682d2ba4b893c9afe"
CLANGMD5SUM = "ff42885ed2ab98f1ecb8c1fc41205343"
LLDMD5SUM = "ae7dc7c027b1fa89b5b013d391d3ee2b"
LLDBMD5SUM = "2e0d44968471fcde980034dbb826bea9"

# remove at next version upgrade or when output changes
PR = "r1"
HASHEQUIV_HASH_VERSION .= ".1"

LLVM_LIBDIR_SUFFIX = "${@d.getVar('baselib').replace('lib', '')}"

# set the default pigz thread
export PIGZ = "-p ${@oe.utils.cpu_count(at_least=2)}"

def get_clang_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var)
    if   re.match('(i.86|athlon|x86.64)$', a):         return 'X86'
    elif re.match('arm$', a):                          return 'ARM'
    elif re.match('armeb$', a):                        return 'ARM'
    elif re.match('aarch64$', a):                      return 'AArch64'
    elif re.match('aarch64_be$', a):                   return 'AArch64'
    elif re.match('mips(isa|)(32|64|)(r6|)(el|)$', a): return 'Mips'
    elif re.match('riscv32$', a):                      return 'RISCV'
    elif re.match('riscv64$', a):                      return 'RISCV'
    elif re.match('p(pc|owerpc)(|64)', a):             return 'PowerPC'
    elif re.match('loongarch64$', a):                  return 'LoongArch'
    else:
        bb.fatal("Unhandled architecture %s" % arch_val)
        return ""

def get_clang_host_arch(bb, d):
    return get_clang_arch(bb, d, 'HOST_ARCH')

def get_clang_target_arch(bb, d):
    return get_clang_arch(bb, d, 'TARGET_ARCH')

# Don't put build-time RPATHs in the binaries, whilst they'll be stripped on
# install they cause non-deterministic binaries.
EXTRA_OECMAKE += "-DCMAKE_BUILD_WITH_INSTALL_RPATH=ON"

# Don't embed found git information into the version string as this
# will include the git server URL.
EXTRA_OECMAKE += "-DLLVM_APPEND_VC_REV=OFF"

require common.inc
