require go-common.inc

FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/go:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=7998cb338f82d15c0eff93b7004d272a"

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
    file://0010-cmd-go-clear-GOROOT-for-func-ldShared-when-trimpath-.patch \
    file://0011-cmd-link-stop-forcing-binutils-gold-dependency-on-aa.patch \
"
SRC_URI[main.sha256sum] = "3172293d04b209dc1144698e7ba13f0477f6ba8c5ffd0be66c20fdbc9785dfbb"
