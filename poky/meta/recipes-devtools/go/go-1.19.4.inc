require go-common.inc

FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/go:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
    file://0001-cmd-go-make-content-based-hash-generation-less-pedan.patch \
    file://0003-allow-GOTOOLDIR-to-be-overridden-in-the-environment.patch \
    file://0004-ld-add-soname-to-shareable-objects.patch \
    file://0005-make.bash-override-CC-when-building-dist-and-go_boot.patch \
    file://0006-cmd-dist-separate-host-and-target-builds.patch \
    file://0007-cmd-go-make-GOROOT-precious-by-default.patch \
    file://0001-exec.go-do-not-write-linker-flags-into-buildids.patch \
    file://0001-src-cmd-dist-buildgo.go-do-not-hardcode-host-compile.patch \
    file://filter-build-paths.patch \
"
SRC_URI[main.sha256sum] = "eda74db4ac494800a3e66ee784e495bfbb9b8e535df924a8b01b1a8028b7f368"
