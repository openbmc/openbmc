SUMMARY = "fd is a simple, fast and user-friendly alternative to find."
HOMEPAGE = "https://crates.io/crates/fd-find"
DESCRIPTION = "fd is a program to find entries in your filesystem. It \
               is a simple, fast and user-friendly alternative to find. \
               While it does not aim to support all of find's powerful \
               functionality, it provides sensible (opinionated) defaults \
               for a majority of use cases."

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE-MIT;md5=96713c739748a44f86272791c89ce344 \
"

SRC_URI = "crate://crates.io/fd-find/${PV};name=fd-find"
SRC_URI += "file://0001-Define-ALIGNOF_MAX_ALIGN_T-for-riscv32.patch;patchdir=../tikv-jemallocator-0.6.0"
SRC_URI[fd-find.sha256sum] = "2fbf004b5bbdefab92e76237e2022c77842cdef5d3213fe09fd804e0474785db"
S = "${CARGO_VENDORING_DIRECTORY}/fd-find-${PV}"

inherit cargo cargo-update-recipe-crates

#Upstream fd-find sets strip = true in [profile.release], which causes Cargo
#to strip the binary during compilation. This interferes with Yocto’s normal
#do_package stripping process.By adding the following flag, we ensure that
#stripping is handled by Yocto as usual:
CARGO_BUILD_FLAGS += " --config profile.release.strip=false"

require ${BPN}-crates.inc

BBCLASSEXTEND = "native"
