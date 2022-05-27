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
"
SRC_URI[main.sha256sum] = "2c44d03ea2c34092137ab919ba602f2c261a038d08eb468528a3f3a28e5667e2"
