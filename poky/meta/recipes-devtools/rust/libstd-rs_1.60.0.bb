require rust-source.inc
require libstd-rs.inc

# Check if libc crate is >= 0.2.17 before dropping this patch
SRC_URI += " \
    file://0001-Add-400-series-syscalls-to-musl-riscv64-definitions.patch;patchdir=../../ \
    file://0001-Update-checksums-for-modified-vendored-libc.patch;patchdir=../../ \
"
# libstd moved from src/libstd to library/std in 1.47+
S = "${RUSTSRC}/library/std"

BBCLASSEXTEND = "nativesdk"