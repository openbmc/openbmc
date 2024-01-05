#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python3native
inherit rust-target-config

# Common variables used by all Rust builds
export rustlibdir = "${libdir}/rustlib/${RUST_HOST_SYS}/lib"
FILES:${PN} += "${rustlibdir}/*.so"
FILES:${PN}-dev += "${rustlibdir}/*.rlib ${rustlibdir}/*.rmeta"
FILES:${PN}-dbg += "${rustlibdir}/.debug"

RUSTLIB = "-L ${STAGING_DIR_HOST}${rustlibdir}"
RUST_DEBUG_REMAP = "--remap-path-prefix=${WORKDIR}=${TARGET_DBGSRC_DIR}"
RUSTFLAGS += "${RUSTLIB} ${RUST_DEBUG_REMAP}"
RUSTLIB_DEP ??= "libstd-rs"
RUST_PANIC_STRATEGY ??= "unwind"

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
    if d.getVar('{}_ARCH'.format(thing)) == d.getVar('TARGET_ARCH') and target_is_armv7(d):
        arch = "armv7"
    else:
        arch = oe.rust.arch_to_rust_arch(d.getVar('{}_ARCH'.format(thing)))

    # Substituting "unknown" when vendor is empty will match rust's standard
    # targets when building native recipes (including rust-native itself)
    vendor = d.getVar('{}_VENDOR'.format(thing)) or "-unknown"

    # Default to glibc
    libc = "-gnu"
    os = d.getVar('{}_OS'.format(thing))
    # This catches ARM targets and appends the necessary hard float bits
    if os == "linux-gnueabi" or os == "linux-musleabi":
        libc = bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hf', '', d)
    elif os == "linux-gnux32" or os == "linux-muslx32":
        libc = ""
    elif "musl" in os:
        libc = "-musl"
        os = "linux"
    elif "elf" in os:
        libc = "-elf"
        os = "none"
    elif "eabi" in os:
        libc = "-eabi"
        os = "none"

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
#   see: https://github.com/rust-lang/cc-rs
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

create_wrapper_rust () {
	file="$1"
	shift
	extras="$1"
	shift
	crate_cc_extras="$1"
	shift

	cat <<- EOF > "${file}"
	#!/usr/bin/env python3
	import os, sys
	orig_binary = "$@"
	extras = "${extras}"

	# Apply a required subset of CC crate compiler flags
	# when we build a target recipe for a non-bare-metal target.
	# https://github.com/rust-lang/cc-rs/blob/main/src/lib.rs#L1614
	if "CRATE_CC_NO_DEFAULTS" in os.environ.keys() and \
	   "TARGET" in os.environ.keys() and not "-none-" in os.environ["TARGET"]:
	    orig_binary += "${crate_cc_extras}"

	binary = orig_binary.split()[0]
	args = orig_binary.split() + sys.argv[1:]
	if extras:
	    args.append(extras)
	os.execvp(binary, args)
	EOF
	chmod +x "${file}"
}

WRAPPER_TARGET_CC = "${CC}"
WRAPPER_TARGET_CXX = "${CXX}"
WRAPPER_TARGET_CCLD = "${CCLD}"
WRAPPER_TARGET_LDFLAGS = "${LDFLAGS}"
WRAPPER_TARGET_EXTRALD = ""
# see recipes-devtools/gcc/gcc/0018-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch
# we need to link with ssp_nonshared on musl to avoid "undefined reference to `__stack_chk_fail_local'"
# when building MACHINE=qemux86 for musl
WRAPPER_TARGET_EXTRALD:libc-musl = "-lssp_nonshared"
WRAPPER_TARGET_AR = "${AR}"

# compiler is used by gcc-rs
# linker is used by rustc/cargo
# archiver is used by the build of libstd-rs
do_rust_create_wrappers () {
	mkdir -p "${WRAPPER_DIR}"

	# Yocto Build / Rust Host C compiler
	create_wrapper_rust "${RUST_BUILD_CC}" "" "${CRATE_CC_FLAGS}" "${BUILD_CC}"
	# Yocto Build / Rust Host C++ compiler
	create_wrapper_rust "${RUST_BUILD_CXX}" "" "${CRATE_CC_FLAGS}" "${BUILD_CXX}"
	# Yocto Build / Rust Host linker
	create_wrapper_rust "${RUST_BUILD_CCLD}" "" "" "${BUILD_CCLD}" "${BUILD_LDFLAGS}"
	# Yocto Build / Rust Host archiver
	create_wrapper_rust "${RUST_BUILD_AR}" "" "" "${BUILD_AR}"

	# Yocto Target / Rust Target C compiler
	create_wrapper_rust "${RUST_TARGET_CC}" "${WRAPPER_TARGET_EXTRALD}" "${CRATE_CC_FLAGS}" "${WRAPPER_TARGET_CC}" "${WRAPPER_TARGET_LDFLAGS}"
	# Yocto Target / Rust Target C++ compiler
	create_wrapper_rust "${RUST_TARGET_CXX}" "${WRAPPER_TARGET_EXTRALD}" "${CRATE_CC_FLAGS}" "${WRAPPER_TARGET_CXX}" "${CXXFLAGS}"
	# Yocto Target / Rust Target linker
	create_wrapper_rust "${RUST_TARGET_CCLD}" "${WRAPPER_TARGET_EXTRALD}" "" "${WRAPPER_TARGET_CCLD}" "${WRAPPER_TARGET_LDFLAGS}"
	# Yocto Target / Rust Target archiver
	create_wrapper_rust "${RUST_TARGET_AR}" "" "" "${WRAPPER_TARGET_AR}"

}

addtask rust_create_wrappers before do_configure after do_patch do_prepare_recipe_sysroot
do_rust_create_wrappers[dirs] += "${WRAPPER_DIR}"
