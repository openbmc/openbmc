require rust-source.inc
require libstd-rs.inc

SRC_URI += " \
    file://0001-Add-base-definitions-for-riscv64-musl.patch;patchdir=../../ \
    file://0002-FIXUP-linux-musl-mod.rs-add-riscv64-to-b64-set.patch;patchdir=../../ \
    file://0003-FIXUP-Correct-definitions-to-match-musl.patch;patchdir=../../ \
    file://0004-Update-checksums-for-modified-files-for-rust.patch;patchdir=../../ \
    "

# libstd moved from src/libstd to library/std in 1.47+
S = "${RUSTSRC}/library/std"
