inherit python3native

# Common variables used by all Rust builds
export rustlibdir = "${libdir}/rust"
FILES:${PN} += "${rustlibdir}/*.so"
FILES:${PN}-dev += "${rustlibdir}/*.rlib ${rustlibdir}/*.rmeta"
FILES:${PN}-dbg += "${rustlibdir}/.debug"

RUSTLIB = "-L ${STAGING_LIBDIR}/rust"
RUST_DEBUG_REMAP = "--remap-path-prefix=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"
RUSTFLAGS += "${RUSTLIB} ${RUST_DEBUG_REMAP}"
RUSTLIB_DEP ?= "libstd-rs"
export RUST_TARGET_PATH = "${STAGING_LIBDIR_NATIVE}/rustlib"
RUST_PANIC_STRATEGY ?= "unwind"

# Native builds are not effected by TCLIBC. Without this, rust-native
# thinks it's "target" (i.e. x86_64-linux) is a musl target.
RUST_LIBC = "${TCLIBC}"
RUST_LIBC:class-crosssdk = "glibc"
RUST_LIBC:class-native = "glibc"

def determine_libc(d, thing):
    '''Determine which libc something should target'''

    # BUILD is never musl, TARGET may be musl or glibc,
    # HOST could be musl, but only if a compiler is built to be run on
    # target in which case HOST_SYS != BUILD_SYS.
    if thing == 'TARGET':
        libc = d.getVar('RUST_LIBC')
    elif thing == 'BUILD' and (d.getVar('HOST_SYS') != d.getVar('BUILD_SYS')):
        libc = d.getVar('RUST_LIBC')
    else:
        libc = d.getVar('RUST_LIBC:class-native')

    return libc

def target_is_armv7(d):
    '''Determine if target is armv7'''
    # TUNE_FEATURES may include arm* even if the target is not arm
    # in the case of *-native packages
    if d.getVar('TARGET_ARCH') != 'arm':
        return False

    feat = d.getVar('TUNE_FEATURES')
    feat = frozenset(feat.split())
    mach_overrides = d.getVar('MACHINEOVERRIDES')
    mach_overrides = frozenset(mach_overrides.split(':'))

    v7=frozenset(['armv7a', 'armv7r', 'armv7m', 'armv7ve'])
    if mach_overrides.isdisjoint(v7) and feat.isdisjoint(v7):
        return False
    else:
        return True
target_is_armv7[vardepvalue] = "${@target_is_armv7(d)}"

# Responsible for taking Yocto triples and converting it to Rust triples
def rust_base_triple(d, thing):
    '''
    Mangle bitbake's *_SYS into something that rust might support (see
    rust/mk/cfg/* for a list)

    Note that os is assumed to be some linux form
    '''

    # The llvm-target for armv7 is armv7-unknown-linux-gnueabihf
    if thing == "TARGET" and target_is_armv7(d):
        arch = "armv7"
    else:
        arch = oe.rust.arch_to_rust_arch(d.getVar('{}_ARCH'.format(thing)))

    # All the Yocto targets are Linux and are 'unknown'
    vendor = "-unknown"
    os = d.getVar('{}_OS'.format(thing))
    libc = determine_libc(d, thing)

    # Prefix with a dash and convert glibc -> gnu
    if libc == "glibc":
        libc = "-gnu"
    elif libc == "musl":
        libc = "-musl"

    # Don't double up musl (only appears to be the case on aarch64)
    if os == "linux-musl":
        if libc != "-musl":
            bb.fatal("{}_OS was '{}' but TCLIBC was not 'musl'".format(thing, os))
        os = "linux"

    # This catches ARM targets and appends the necessary hard float bits
    if os == "linux-gnueabi" or os == "linux-musleabi":
        libc = bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hf', '', d)
    return arch + vendor + '-' + os + libc


# In some cases uname and the toolchain differ on their idea of the arch name
RUST_BUILD_ARCH = "${@oe.rust.arch_to_rust_arch(d.getVar('BUILD_ARCH'))}"

# Naming explanation
# Yocto
# - BUILD_SYS - Yocto triple of the build environment
# - HOST_SYS - What we're building for in Yocto
# - TARGET_SYS - What we're building for in Yocto
#
# So when building '-native' packages BUILD_SYS == HOST_SYS == TARGET_SYS
# When building packages for the image HOST_SYS == TARGET_SYS
# This is a gross over simplification as there are other modes but
# currently this is all that's supported.
#
# Rust
# - TARGET - the system where the binary will run
# - HOST - the system where the binary is being built
#
# Rust additionally will use two additional cases:
# - undecorated (e.g. CC) - equivalent to TARGET
# - triple suffix (e.g. CC:x86_64_unknown_linux_gnu) - both
#   see: https://github.com/alexcrichton/gcc-rs
# The way that Rust's internal triples and Yocto triples are mapped together
# its likely best to not use the triple suffix due to potential confusion.

RUST_BUILD_SYS = "${@rust_base_triple(d, 'BUILD')}"
RUST_BUILD_SYS[vardepvalue] = "${RUST_BUILD_SYS}"
RUST_HOST_SYS = "${@rust_base_triple(d, 'HOST')}"
RUST_HOST_SYS[vardepvalue] = "${RUST_HOST_SYS}"
RUST_TARGET_SYS = "${@rust_base_triple(d, 'TARGET')}"
RUST_TARGET_SYS[vardepvalue] = "${RUST_TARGET_SYS}"

# wrappers to get around the fact that Rust needs a single
# binary but Yocto's compiler and linker commands have
# arguments. Technically the archiver is always one command but
# this is necessary for builds that determine the prefix and then
# use those commands based on the prefix.
WRAPPER_DIR = "${WORKDIR}/wrapper"
RUST_BUILD_CC = "${WRAPPER_DIR}/build-rust-cc"
RUST_BUILD_CXX = "${WRAPPER_DIR}/build-rust-cxx"
RUST_BUILD_CCLD = "${WRAPPER_DIR}/build-rust-ccld"
RUST_BUILD_AR = "${WRAPPER_DIR}/build-rust-ar"
RUST_TARGET_CC = "${WRAPPER_DIR}/target-rust-cc"
RUST_TARGET_CXX = "${WRAPPER_DIR}/target-rust-cxx"
RUST_TARGET_CCLD = "${WRAPPER_DIR}/target-rust-ccld"
RUST_TARGET_AR = "${WRAPPER_DIR}/target-rust-ar"

create_wrapper () {
	file="$1"
	shift

	cat <<- EOF > "${file}"
	#!/usr/bin/env python3
	import os, sys
	orig_binary = "$@"
	binary = orig_binary.split()[0]
	args = orig_binary.split() + sys.argv[1:]
	os.execvp(binary, args)
	EOF
	chmod +x "${file}"
}

export WRAPPER_TARGET_CC = "${CC}"
export WRAPPER_TARGET_CXX = "${CXX}"
export WRAPPER_TARGET_CCLD = "${CCLD}"
export WRAPPER_TARGET_LDFLAGS = "${LDFLAGS}"
export WRAPPER_TARGET_AR = "${AR}"

# compiler is used by gcc-rs
# linker is used by rustc/cargo
# archiver is used by the build of libstd-rs
do_rust_create_wrappers () {
	mkdir -p "${WRAPPER_DIR}"

	# Yocto Build / Rust Host C compiler
	create_wrapper "${RUST_BUILD_CC}" "${BUILD_CC}"
	# Yocto Build / Rust Host C++ compiler
	create_wrapper "${RUST_BUILD_CXX}" "${BUILD_CXX}"
	# Yocto Build / Rust Host linker
	create_wrapper "${RUST_BUILD_CCLD}" "${BUILD_CCLD}" "${BUILD_LDFLAGS}"
	# Yocto Build / Rust Host archiver
	create_wrapper "${RUST_BUILD_AR}" "${BUILD_AR}"

	# Yocto Target / Rust Target C compiler
	create_wrapper "${RUST_TARGET_CC}" "${WRAPPER_TARGET_CC}" "${WRAPPER_TARGET_LDFLAGS}"
	# Yocto Target / Rust Target C++ compiler
	create_wrapper "${RUST_TARGET_CXX}" "${WRAPPER_TARGET_CXX}"
	# Yocto Target / Rust Target linker
	create_wrapper "${RUST_TARGET_CCLD}" "${WRAPPER_TARGET_CCLD}" "${WRAPPER_TARGET_LDFLAGS}"
	# Yocto Target / Rust Target archiver
	create_wrapper "${RUST_TARGET_AR}" "${WRAPPER_TARGET_AR}"

}

addtask rust_create_wrappers before do_configure after do_patch do_prepare_recipe_sysroot
do_rust_create_wrappers[dirs] += "${WRAPPER_DIR}"
