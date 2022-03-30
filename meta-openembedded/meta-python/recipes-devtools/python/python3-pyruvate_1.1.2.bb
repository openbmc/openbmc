SUMMARY = "WSGI server implemented in Rust."
DESCRIPTION = "Pyruvate is a reasonably fast, multithreaded, non-blocking \
WSGI server implemented in Rust."
HOMEPAGE = "https://gitlab.com/tschorr/pyruvate"
BUGTRACKER = "https://gitlab.com/tschorr/pyruvate/-/issues"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=051b48e640a6e2d795eac75542d9417c \
                    file://LICENSE.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI[sha256sum] = "10befedd97e73fc18b902d02aa3b24e8978aa162242c1b664849c886c0675899"

S = "${WORKDIR}/pyruvate-${PV}"

inherit pypi python_setuptools3_rust

PIP_INSTALL_DIST_PATH = "${S}/dist"

# crossbeam-* -> std::sync::atomic AtomicI64, AtomicU64
# not supported on mips/powerpc with 32-bit pointers
# https://doc.rust-lang.org/std/sync/atomic/#portability
RUSTFLAGS:append:mips = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:mipsel = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:powerpc = " --cfg crossbeam_no_atomic_64"
RUSTFLAGS:append:riscv32 = " --cfg crossbeam_no_atomic_64"

SRC_URI += " \
    crate://crates.io/aho-corasick/0.7.18 \
    crate://crates.io/atty/0.2.14 \
    crate://crates.io/autocfg/1.0.1 \
    crate://crates.io/bitflags/1.3.2 \
    crate://crates.io/block-buffer/0.9.0 \
    crate://crates.io/cc/1.0.72 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/chrono/0.4.19 \
    crate://crates.io/cpufeatures/0.2.1 \
    crate://crates.io/cpython/0.7.0 \
    crate://crates.io/crossbeam-channel/0.5.2 \
    crate://crates.io/crossbeam-deque/0.8.1 \
    crate://crates.io/crossbeam-epoch/0.9.6 \
    crate://crates.io/crossbeam-queue/0.3.3 \
    crate://crates.io/crossbeam-utils/0.8.6 \
    crate://crates.io/crossbeam/0.8.1 \
    crate://crates.io/crypto-mac/0.11.1 \
    crate://crates.io/ctrlc/3.2.1 \
    crate://crates.io/digest/0.9.0 \
    crate://crates.io/encoding-index-japanese/1.20141219.5 \
    crate://crates.io/encoding-index-korean/1.20141219.5 \
    crate://crates.io/encoding-index-simpchinese/1.20141219.5 \
    crate://crates.io/encoding-index-singlebyte/1.20141219.5 \
    crate://crates.io/encoding-index-tradchinese/1.20141219.5 \
    crate://crates.io/encoding/0.2.33 \
    crate://crates.io/encoding_index_tests/0.1.4 \
    crate://crates.io/env_logger/0.9.0 \
    crate://crates.io/errno-dragonfly/0.1.2 \
    crate://crates.io/errno/0.2.8 \
    crate://crates.io/fastrand/1.6.0 \
    crate://crates.io/generic-array/0.14.5 \
    crate://crates.io/getrandom/0.2.3 \
    crate://crates.io/hermit-abi/0.1.19 \
    crate://crates.io/hmac/0.11.0 \
    crate://crates.io/httparse/1.5.1 \
    crate://crates.io/humantime/2.1.0 \
    crate://crates.io/instant/0.1.12 \
    crate://crates.io/lazy_static/1.4.0 \
    crate://crates.io/libc/0.2.120 \
    crate://crates.io/libsystemd/0.4.1 \
    crate://crates.io/log/0.4.14 \
    crate://crates.io/memchr/2.4.1 \
    crate://crates.io/memoffset/0.6.5 \
    crate://crates.io/mio/0.8.0 \
    crate://crates.io/miow/0.3.7 \
    crate://crates.io/nix/0.23.1 \
    crate://crates.io/ntapi/0.3.6 \
    crate://crates.io/num-integer/0.1.44 \
    crate://crates.io/num-traits/0.2.14 \
    crate://crates.io/num_cpus/1.13.1 \
    crate://crates.io/once_cell/1.9.0 \
    crate://crates.io/opaque-debug/0.3.0 \
    crate://crates.io/paste/1.0.6 \
    crate://crates.io/ppv-lite86/0.2.16 \
    crate://crates.io/proc-macro2/1.0.36 \
    crate://crates.io/python3-sys/0.7.0 \
    crate://crates.io/quote/1.0.14 \
    crate://crates.io/rand/0.8.4 \
    crate://crates.io/rand_chacha/0.3.1 \
    crate://crates.io/rand_core/0.6.3 \
    crate://crates.io/rand_hc/0.3.1 \
    crate://crates.io/redox_syscall/0.2.10 \
    crate://crates.io/regex-syntax/0.6.25 \
    crate://crates.io/regex/1.5.4 \
    crate://crates.io/remove_dir_all/0.5.3 \
    crate://crates.io/scopeguard/1.1.0 \
    crate://crates.io/serde/1.0.133 \
    crate://crates.io/serde_derive/1.0.133 \
    crate://crates.io/sha2/0.9.9 \
    crate://crates.io/simplelog/0.11.1 \
    crate://crates.io/spmc/0.3.0 \
    crate://crates.io/subtle/2.4.1 \
    crate://crates.io/syn/1.0.85 \
    crate://crates.io/tempfile/3.3.0 \
    crate://crates.io/termcolor/1.1.2 \
    crate://crates.io/thiserror-impl/1.0.30 \
    crate://crates.io/thiserror/1.0.30 \
    crate://crates.io/threadpool/1.8.1 \
    crate://crates.io/time/0.1.44 \
    crate://crates.io/typenum/1.15.0 \
    crate://crates.io/unicode-xid/0.2.2 \
    crate://crates.io/urlencoding/2.1.0 \
    crate://crates.io/uuid/0.8.2 \
    crate://crates.io/version_check/0.9.4 \
    crate://crates.io/wasi/0.10.0+wasi-snapshot-preview1 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.5 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.3.9 \
"
SRC_URI += "\
            file://0001-linux.rs-Define-consts-for-rv32-architecture.patch;patchdir=../cargo_home/bitbake/nix-0.23.1/ \
            "
SRC_URI:append:mips = " file://0001-check-for-mips-targets-for-stat.st_dev-definitions.patch;patchdir=../cargo_home/bitbake/libsystemd-0.4.1/"

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
