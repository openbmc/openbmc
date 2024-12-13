SUMMARY = "A VK-GL-CTS/dEQP wrapper program to parallelize it across CPUs and report results against a baseline."
HOMEPAGE = "https://gitlab.freedesktop.org/mesa/deqp-runner"
LICENSE = "MIT"

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=4f59d6446bf2e004e80df1a0937129fa\
"

inherit cargo cargo-update-recipe-crates

SRC_URI += " \
    crate://crates.io/deqp-runner/0.20.0 \
    file://0001-deqp-runner-drop-zstd-support.patch \
"

SRC_URI[deqp-runner-0.20.0.sha256sum] = "a3f4fab1179a01cbbdbe4b93e0a040f74de7b9086498d91976d93844a31439dd"

require deqp-runner-crates.inc
