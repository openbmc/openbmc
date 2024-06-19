require go-common.inc

FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/go:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
    file://0001-cmd-go-make-content-based-hash-generation-less-pedan.patch \
    file://0002-cmd-go-Allow-GOTOOLDIR-to-be-overridden-in-the-envir.patch \
    file://0003-ld-add-soname-to-shareable-objects.patch \
    file://0004-make.bash-override-CC-when-building-dist-and-go_boot.patch \
    file://0005-cmd-dist-separate-host-and-target-builds.patch \
    file://0006-cmd-go-make-GOROOT-precious-by-default.patch \
    file://0007-exec.go-filter-out-build-specific-paths-from-linker-.patch \
    file://0008-src-cmd-dist-buildgo.go-do-not-hardcode-host-compile.patch \
    file://0009-go-Filter-build-paths-on-staticly-linked-arches.patch \
"
SRC_URI[main.sha256sum] = "80648ef34f903193d72a59c0dff019f5f98ae0c9aa13ade0b0ecbff991a76f68"
