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
"

SRC_URI[deqp-runner-0.20.2.sha256sum] = "600ea527945ea2d0c5d2987d6adb4d2944731ea95906de2c94eb419148cc398b"

require deqp-runner-crates.inc
