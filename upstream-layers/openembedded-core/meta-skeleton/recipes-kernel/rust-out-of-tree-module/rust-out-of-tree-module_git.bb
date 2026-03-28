SUMMARY = "Basic template for an out-of-tree Linux kernel module written in Rust"
HOMEPAGE = "https://github.com/Rust-for-Linux/rust-out-of-tree-module"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit module-rust

SRC_URI = "git://github.com/Rust-for-Linux/rust-out-of-tree-module.git;protocol=https;branch=main"
SRCREV = "00b5a8ee2bf53532d115004d7636b61a54f49802"
UPSTREAM_CHECK_COMMITS = "1"

EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_DIR}"

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.
RPROVIDES:${PN} += "kernel-module-rust-out-of-tree"
