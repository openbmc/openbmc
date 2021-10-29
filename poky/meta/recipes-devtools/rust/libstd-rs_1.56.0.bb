require rust-source.inc
require libstd-rs.inc

SRC_URI += " \
    file://0005-Add-base-definitions-for-riscv64-musl-libc.patch;patchdir=../../ \
    file://0006-FIXUP-linux-musl-mod.rs-add-riscv64-to-b64-set.patch;patchdir=../../ \
    file://0007-FIXUP-Correct-definitions-to-match-musl.patch;patchdir=../../ \
    file://0008-Update-checksums-for-modified-files-for-rust.patch;patchdir=../../ \
"
# libstd moved from src/libstd to library/std in 1.47+
S = "${RUSTSRC}/library/std"
