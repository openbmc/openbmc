SUMMARY = "WSGI server implemented in Rust."
DESCRIPTION = "Pyruvate is a reasonably fast, multithreaded, non-blocking \
WSGI server implemented in Rust."
HOMEPAGE = "https://gitlab.com/tschorr/pyruvate"
BUGTRACKER = "https://gitlab.com/tschorr/pyruvate/-/issues"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += "\
    git://gitlab.com/tschorr/pyruvate.git;protocol=https;branch=main \
    file://0001-linux.rs-Define-consts-for-rv32-architecture.patch;patchdir=../cargo_home/bitbake/nix-0.23.2/ \
    file://0001-musl-Define-SOCK_NONBLOCK-with-O_NONBLOCK.patch;patchdir=../cargo_home/bitbake/libc-0.2.138/ \
    file://0001-musl-riscv32-Define-F_SETLK-F_SETLKW-and-fix-F_GETLK.patch;patchdir=../cargo_home/bitbake/libc-0.2.138/ \
    file://0001-musl-Move-F_OFD_GETLK-F_OFD_SETLK-and-F_OFD_SETLKW-t.patch;patchdir=../cargo_home/bitbake/libc-0.2.138/ \
    file://0001-musl-Define-O_LARGEFILE-for-riscv32.patch;patchdir=../cargo_home/bitbake/libc-0.2.138/ \
    file://0001-musl-Define-SOCK_SEQPACKET-in-common-place.patch;patchdir=../cargo_home/bitbake/libc-0.2.138/ \
"
SRCREV = "57db64c9f65ced05c71b8d786c1cedfaa2991597"

SRC_URI:append:mips = " file://0001-check-for-mips-targets-for-stat.st_dev-definitions.patch;patchdir=../cargo_home/bitbake/libsystemd-0.5.0/"

S = "${WORKDIR}/git"

inherit python_setuptools3_rust cargo-update-recipe-crates

PIP_INSTALL_DIST_PATH = "${S}/dist"

# crossbeam-* -> std::sync::atomic AtomicI64, AtomicU64
# not supported on mips/powerpc with 32-bit pointers
# https://doc.rust-lang.org/std/sync/atomic/#portability
RUSTFLAGS:append:mips = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:mipsel = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:powerpc = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:riscv32 = " --cfg crossbeam_no_atomic_64"

require ${BPN}-crates.inc

# The following configs & dependencies are from setuptools extras_require.
# These dependencies are optional, hence can be controlled via PACKAGECONFIG.
# The upstream names may not correspond exactly to bitbake package names.
#
# Uncomment this line to enable all the optional features.
#PACKAGECONFIG ?= "test"
PACKAGECONFIG[test] = ",,,python3-pytest python3-requests"

# WARNING: the following rdepends are determined through basic analysis of the
# python sources, and might not be 100% accurate.
RDEPENDS:${PN} += "python3-core"
