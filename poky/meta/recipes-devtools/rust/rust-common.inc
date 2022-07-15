
# Right now this is focused on arm-specific tune features.
# We get away with this for now as one can only use x86-64 as the build host
# (not arm).
# Note that TUNE_FEATURES is _always_ refering to the target, so we really
# don't want to use this for the host/build.
def llvm_features_from_tune(d):
    f = []
    feat = d.getVar('TUNE_FEATURES')
    if not feat:
        return []
    feat = frozenset(feat.split())

    mach_overrides = d.getVar('MACHINEOVERRIDES')
    mach_overrides = frozenset(mach_overrides.split(':'))

    if 'vfpv4' in feat:
        f.append("+vfp4")
    if 'vfpv3' in feat:
        f.append("+vfp3")
    if 'vfpv3d16' in feat:
        f.append("+d16")

    if 'vfpv2' in feat or 'vfp' in feat:
        f.append("+vfp2")

    if 'neon' in feat:
        f.append("+neon")

    if 'mips32' in feat:
        f.append("+mips32")

    if 'mips32r2' in feat:
        f.append("+mips32r2")

    if target_is_armv7(d):
        f.append('+v7')

    if ('armv6' in mach_overrides) or ('armv6' in feat):
        f.append("+v6")
    if 'armv5te' in feat:
        f.append("+strict-align")
        f.append("+v5te")
    elif 'armv5' in feat:
        f.append("+strict-align")
        f.append("+v5")

    if ('armv4' in mach_overrides) or ('armv4' in feat):
        f.append("+strict-align")

    if 'dsp' in feat:
        f.append("+dsp")

    if 'thumb' in feat:
        if d.getVar('ARM_THUMB_OPT') == "thumb":
            if target_is_armv7(d):
                f.append('+thumb2')
            f.append("+thumb-mode")

    if 'cortexa5' in feat:
        f.append("+a5")
    if 'cortexa7' in feat:
        f.append("+a7")
    if 'cortexa9' in feat:
        f.append("+a9")
    if 'cortexa15' in feat:
        f.append("+a15")
    if 'cortexa17' in feat:
        f.append("+a17")
    if ('riscv64' in feat) or ('riscv32' in feat):
        f.append("+a,+c,+d,+f,+m")
    return f
llvm_features_from_tune[vardepvalue] = "${@llvm_features_from_tune(d)}"

# TARGET_CC_ARCH changes from build/cross/target so it'll do the right thing
# this should go away when https://github.com/rust-lang/rust/pull/31709 is
# stable (1.9.0?)
def llvm_features_from_cc_arch(d):
    f = []
    feat = d.getVar('TARGET_CC_ARCH')
    if not feat:
        return []
    feat = frozenset(feat.split())

    if '-mmmx' in feat:
        f.append("+mmx")
    if '-msse' in feat:
        f.append("+sse")
    if '-msse2' in feat:
        f.append("+sse2")
    if '-msse3' in feat:
        f.append("+sse3")
    if '-mssse3' in feat:
        f.append("+ssse3")
    if '-msse4.1' in feat:
        f.append("+sse4.1")
    if '-msse4.2' in feat:
        f.append("+sse4.2")
    if '-msse4a' in feat:
        f.append("+sse4a")
    if '-mavx' in feat:
        f.append("+avx")
    if '-mavx2' in feat:
        f.append("+avx2")

    return f

def llvm_features_from_target_fpu(d):
    # TARGET_FPU can be hard or soft. +soft-float tell llvm to use soft float
    # ABI. There is no option for hard.

    fpu = d.getVar('TARGET_FPU', True)
    return ["+soft-float"] if fpu == "soft" else []

def llvm_features(d):
    return ','.join(llvm_features_from_tune(d) +
                    llvm_features_from_cc_arch(d) +
                    llvm_features_from_target_fpu(d))


## arm-unknown-linux-gnueabihf
DATA_LAYOUT[arm] = "e-m:e-p:32:32-i64:64-v128:64:128-a:0:32-n32-S64"
TARGET_ENDIAN[arm] = "little"
TARGET_POINTER_WIDTH[arm] = "32"
TARGET_C_INT_WIDTH[arm] = "32"
MAX_ATOMIC_WIDTH[arm] = "64"
FEATURES[arm] = "+v6,+vfp2"

## armv7-unknown-linux-gnueabihf
DATA_LAYOUT[armv7-eabi] = "e-m:e-p:32:32-i64:64-v128:64:128-a:0:32-n32-S64"
TARGET_ENDIAN[armv7-eabi] = "little"
TARGET_POINTER_WIDTH[armv7-eabi] = "32"
TARGET_C_INT_WIDTH[armv7-eabi] = "32"
MAX_ATOMIC_WIDTH[armv7-eabi] = "64"
FEATURES[armv7-eabi] = "+v7,+vfp2,+thumb2"

## aarch64-unknown-linux-{gnu, musl}
DATA_LAYOUT[aarch64] = "e-m:e-i8:8:32-i16:16:32-i64:64-i128:128-n32:64-S128"
TARGET_ENDIAN[aarch64] = "little"
TARGET_POINTER_WIDTH[aarch64] = "64"
TARGET_C_INT_WIDTH[aarch64] = "32"
MAX_ATOMIC_WIDTH[aarch64] = "128"

## x86_64-unknown-linux-{gnu, musl}
DATA_LAYOUT[x86_64] = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
TARGET_ENDIAN[x86_64] = "little"
TARGET_POINTER_WIDTH[x86_64] = "64"
TARGET_C_INT_WIDTH[x86_64] = "32"
MAX_ATOMIC_WIDTH[x86_64] = "64"

## x86_64-unknown-linux-gnux32
DATA_LAYOUT[x86_64-x32] = "e-m:e-p:32:32-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
TARGET_ENDIAN[x86_64-x32] = "little"
TARGET_POINTER_WIDTH[x86_64-x32] = "32"
TARGET_C_INT_WIDTH[x86_64-x32] = "32"
MAX_ATOMIC_WIDTH[x86_64-x32] = "64"

## i686-unknown-linux-{gnu, musl}
DATA_LAYOUT[i686] = "e-m:e-p:32:32-f64:32:64-f80:32-n8:16:32-S128"
TARGET_ENDIAN[i686] = "little"
TARGET_POINTER_WIDTH[i686] = "32"
TARGET_C_INT_WIDTH[i686] = "32"
MAX_ATOMIC_WIDTH[i686] = "64"

## XXX: a bit of a hack so qemux86 builds, clone of i686-unknown-linux-{gnu, musl} above
DATA_LAYOUT[i586] = "e-m:e-p:32:32-f64:32:64-f80:32-n8:16:32-S128"
TARGET_ENDIAN[i586] = "little"
TARGET_POINTER_WIDTH[i586] = "32"
TARGET_C_INT_WIDTH[i586] = "32"
MAX_ATOMIC_WIDTH[i586] = "64"

## mips-unknown-linux-{gnu, musl}
DATA_LAYOUT[mips] = "E-m:m-p:32:32-i8:8:32-i16:16:32-i64:64-n32-S64"
TARGET_ENDIAN[mips] = "big"
TARGET_POINTER_WIDTH[mips] = "32"
TARGET_C_INT_WIDTH[mips] = "32"
MAX_ATOMIC_WIDTH[mips] = "32"

## mipsel-unknown-linux-{gnu, musl}
DATA_LAYOUT[mipsel] = "e-m:m-p:32:32-i8:8:32-i16:16:32-i64:64-n32-S64"
TARGET_ENDIAN[mipsel] = "little"
TARGET_POINTER_WIDTH[mipsel] = "32"
TARGET_C_INT_WIDTH[mipsel] = "32"
MAX_ATOMIC_WIDTH[mipsel] = "32"

## mips64-unknown-linux-{gnu, musl}
DATA_LAYOUT[mips64] = "E-m:e-i8:8:32-i16:16:32-i64:64-n32:64-S128"
TARGET_ENDIAN[mips64] = "big"
TARGET_POINTER_WIDTH[mips64] = "64"
TARGET_C_INT_WIDTH[mips64] = "64"
MAX_ATOMIC_WIDTH[mips64] = "64"

## mips64el-unknown-linux-{gnu, musl}
DATA_LAYOUT[mips64el] = "e-m:e-i8:8:32-i16:16:32-i64:64-n32:64-S128"
TARGET_ENDIAN[mips64el] = "little"
TARGET_POINTER_WIDTH[mips64el] = "64"
TARGET_C_INT_WIDTH[mips64el] = "64"
MAX_ATOMIC_WIDTH[mips64el] = "64"

## powerpc-unknown-linux-{gnu, musl}
DATA_LAYOUT[powerpc] = "E-m:e-p:32:32-i64:64-n32"
TARGET_ENDIAN[powerpc] = "big"
TARGET_POINTER_WIDTH[powerpc] = "32"
TARGET_C_INT_WIDTH[powerpc] = "32"
MAX_ATOMIC_WIDTH[powerpc] = "32"

## powerpc64-unknown-linux-{gnu, musl}
DATA_LAYOUT[powerpc64] = "E-m:e-i64:64-n32:64-S128-v256:256:256-v512:512:512"
TARGET_ENDIAN[powerpc64] = "big"
TARGET_POINTER_WIDTH[powerpc64] = "64"
TARGET_C_INT_WIDTH[powerpc64] = "64"
MAX_ATOMIC_WIDTH[powerpc64] = "64"

## powerpc64le-unknown-linux-{gnu, musl}
DATA_LAYOUT[powerpc64le] = "e-m:e-i64:64-n32:64-v256:256:256-v512:512:512"
TARGET_ENDIAN[powerpc64le] = "little"
TARGET_POINTER_WIDTH[powerpc64le] = "64"
TARGET_C_INT_WIDTH[powerpc64le] = "64"
MAX_ATOMIC_WIDTH[powerpc64le] = "64"

## riscv32-unknown-linux-{gnu, musl}
DATA_LAYOUT[riscv32] = "e-m:e-p:32:32-i64:64-n32-S128"
TARGET_ENDIAN[riscv32] = "little"
TARGET_POINTER_WIDTH[riscv32] = "32"
TARGET_C_INT_WIDTH[riscv32] = "32"
MAX_ATOMIC_WIDTH[riscv32] = "32"

## riscv64-unknown-linux-{gnu, musl}
DATA_LAYOUT[riscv64] = "e-m:e-p:64:64-i64:64-i128:128-n64-S128"
TARGET_ENDIAN[riscv64] = "little"
TARGET_POINTER_WIDTH[riscv64] = "64"
TARGET_C_INT_WIDTH[riscv64] = "64"
MAX_ATOMIC_WIDTH[riscv64] = "64"

def sys_for(d, thing):
    return d.getVar('{}_SYS'.format(thing))

def prefix_for(d, thing):
    return d.getVar('{}_PREFIX'.format(thing))

# Convert a normal arch (HOST_ARCH, TARGET_ARCH, BUILD_ARCH, etc) to something
# rust's internals won't choke on.
def arch_to_rust_target_arch(arch):
    if arch == "i586" or arch == "i686":
        return "x86"
    elif arch == "mipsel":
        return "mips"
    elif arch == "mip64sel":
        return "mips64"
    elif arch == "armv7":
        return "arm"
    elif arch == "powerpc64le":
        return "powerpc64"
    else:
        return arch

# generates our target CPU value
def llvm_cpu(d):
    cpu = d.getVar('PACKAGE_ARCH')
    target = d.getVar('TRANSLATED_TARGET_ARCH')

    trans = {}
    trans['corei7-64'] = "corei7"
    trans['core2-32'] = "core2"
    trans['x86-64'] = "x86-64"
    trans['i686'] = "i686"
    trans['i586'] = "i586"
    trans['powerpc'] = "powerpc"
    trans['mips64'] = "mips64"
    trans['mips64el'] = "mips64"
    trans['riscv64'] = "generic-rv64"
    trans['riscv32'] = "generic-rv32"

    if target in ["mips", "mipsel"]:
        feat = frozenset(d.getVar('TUNE_FEATURES').split())
        if "mips32r2" in feat:
            trans['mipsel'] = "mips32r2"
            trans['mips'] = "mips32r2"
        elif "mips32" in feat:
            trans['mipsel'] = "mips32"
            trans['mips'] = "mips32"

    try:
        return trans[cpu]
    except:
        return trans.get(target, "generic")

TARGET_LLVM_CPU="${@llvm_cpu(d)}"
TARGET_LLVM_FEATURES = "${@llvm_features(d)}"

# class-native implies TARGET=HOST, and TUNE_FEATURES only describes the real
# (original) target.
TARGET_LLVM_FEATURES:class-native = "${@','.join(llvm_features_from_cc_arch(d))}"

def rust_gen_target(d, thing, wd, features, cpu, arch, abi=""):
    import json
    sys = sys_for(d, thing)
    prefix = prefix_for(d, thing)

    rust_arch = oe.rust.arch_to_rust_arch(arch)

    if abi:
        arch_abi = "{}-{}".format(rust_arch, abi)
    else:
        arch_abi = rust_arch

    features = features or d.getVarFlag('FEATURES', arch_abi) or ""
    features = features.strip()

    # build tspec
    tspec = {}
    tspec['llvm-target'] = d.getVar('RUST_TARGET_SYS', arch_abi)
    tspec['data-layout'] = d.getVarFlag('DATA_LAYOUT', arch_abi)
    tspec['max-atomic-width'] = int(d.getVarFlag('MAX_ATOMIC_WIDTH', arch_abi))
    tspec['target-pointer-width'] = d.getVarFlag('TARGET_POINTER_WIDTH', arch_abi)
    tspec['target-c-int-width'] = d.getVarFlag('TARGET_C_INT_WIDTH', arch_abi)
    tspec['target-endian'] = d.getVarFlag('TARGET_ENDIAN', arch_abi)
    tspec['arch'] = arch_to_rust_target_arch(rust_arch)
    tspec['os'] = "linux"
    if "musl" in tspec['llvm-target']:
        tspec['env'] = "musl"
    else:
        tspec['env'] = "gnu"
    if "riscv64" in tspec['llvm-target']:
        tspec['llvm-abiname'] = "lp64d"
    if "riscv32" in tspec['llvm-target']:
        tspec['llvm-abiname'] = "ilp32d"
    tspec['vendor'] = "unknown"
    tspec['target-family'] = "unix"
    tspec['linker'] = "{}{}gcc".format(d.getVar('CCACHE'), prefix)
    tspec['cpu'] = cpu
    if features != "":
        tspec['features'] = features
    tspec['dynamic-linking'] = True
    tspec['executables'] = True
    tspec['linker-is-gnu'] = True
    tspec['linker-flavor'] = "gcc"
    tspec['has-rpath'] = True
    tspec['has-elf-tls'] = True
    tspec['position-independent-executables'] = True
    tspec['panic-strategy'] = d.getVar("RUST_PANIC_STRATEGY")

    # write out the target spec json file
    with open(wd + sys + '.json', 'w') as f:
        json.dump(tspec, f, indent=4)

# These are accounted for in tmpdir path names so don't need to be in the task sig
rust_gen_target[vardepsexclude] += "RUST_HOST_SYS RUST_TARGET_SYS"

do_rust_gen_targets[vardeps] += "DATA_LAYOUT TARGET_ENDIAN TARGET_POINTER_WIDTH TARGET_C_INT_WIDTH MAX_ATOMIC_WIDTH FEATURES"

python do_rust_gen_targets () {
    wd = d.getVar('WORKDIR') + '/targets/'
    build_arch = d.getVar('BUILD_ARCH')
    rust_gen_target(d, 'BUILD', wd, "", "generic", build_arch)
}

addtask rust_gen_targets after do_patch before do_compile
do_rust_gen_targets[dirs] += "${WORKDIR}/targets"

