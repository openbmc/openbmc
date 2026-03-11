SUMMARY = "A VK-GL-CTS/dEQP wrapper program to parallelize it across CPUs and report results against a baseline."
HOMEPAGE = "https://gitlab.freedesktop.org/mesa/deqp-runner"
LICENSE = "MIT"

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=4f59d6446bf2e004e80df1a0937129fa\
"

inherit cargo cargo-update-recipe-crates

SRC_URI += " \
    crate://crates.io/deqp-runner/${PV} \
    file://0001-deqp-runner-drop-zstd-support.patch \
    file://0001-cargo-Update-tempfile-to-latest-3.x.patch \
    file://0001-Define-more-ioctl-codes-on-riscv32gc-unknown-linux-g.patch;patchdir=${CARGO_VENDORING_DIRECTORY}/libc-0.2.171 \
"
SRC_URI[deqp-runner-0.20.3.sha256sum] = "2de4b135ed68a7f821deeedebb4084d33058b0307f1f9935e2c960430f7532e8"

require deqp-runner-crates.inc

RUSTFLAGS:append:riscv32 = " --cfg getrandom_backend=\"linux_raw\""
