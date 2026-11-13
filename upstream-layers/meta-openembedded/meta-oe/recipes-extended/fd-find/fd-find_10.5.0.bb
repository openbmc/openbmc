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
SRC_URI[fd-find.sha256sum] = "f35becce49c551724fd26d9824ec01d6578868cecbcc4e3d060658f9f8ad1e4d"
S = "${CARGO_VENDORING_DIRECTORY}/fd-find-${PV}"

inherit cargo cargo-update-recipe-crates

#Upstream fd-find sets strip = true in [profile.release], which causes Cargo
#to strip the binary during compilation. This interferes with Yocto’s normal
#do_package stripping process.By adding the following flag, we ensure that
#stripping is handled by Yocto as usual:
CARGO_BUILD_FLAGS += " --config profile.release.strip=false"

require ${BPN}-crates.inc

BBCLASSEXTEND = "native"
