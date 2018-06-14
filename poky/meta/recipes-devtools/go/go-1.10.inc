require go-common.inc

GO_BASEVERSION = "1.10"
GO_MINOR = ".3"
PV .= "${GO_MINOR}"
FILESEXTRAPATHS_prepend := "${FILE_DIRNAME}/go-${GO_BASEVERSION}:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
    file://0001-allow-CC-and-CXX-to-have-multiple-words.patch \
    file://0002-cmd-go-make-content-based-hash-generation-less-pedan.patch \
    file://0003-allow-GOTOOLDIR-to-be-overridden-in-the-environment.patch \
    file://0004-ld-add-soname-to-shareable-objects.patch \
    file://0005-make.bash-override-CC-when-building-dist-and-go_boot.patch \
    file://0006-cmd-dist-separate-host-and-target-builds.patch \
    file://0007-cmd-go-make-GOROOT-precious-by-default.patch \
    file://0008-cmd-internal-obj-arm64-fix-branch-too-far-with-TBZ-l.patch \
"

SRC_URI_append_libc-musl = " file://0009-ld-replace-glibc-dynamic-linker-with-musl.patch"

SRC_URI[main.md5sum] = "d15dfb264105c5e84fbe33f4a4aa5021"
SRC_URI[main.sha256sum] = "567b1cc66c9704d1c019c50bef946272e911ec6baf244310f87f4e678be155f2"
