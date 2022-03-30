require go-common.inc

FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/go-1.18:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
    file://0001-allow-CC-and-CXX-to-have-multiple-words.patch \
    file://0002-cmd-go-make-content-based-hash-generation-less-pedan.patch \
    file://0003-allow-GOTOOLDIR-to-be-overridden-in-the-environment.patch \
    file://0004-ld-add-soname-to-shareable-objects.patch \
    file://0005-make.bash-override-CC-when-building-dist-and-go_boot.patch \
    file://0006-cmd-dist-separate-host-and-target-builds.patch \
    file://0007-cmd-go-make-GOROOT-precious-by-default.patch \
    file://0008-use-GOBUILDMODE-to-set-buildmode.patch \
    file://0009-Revert-cmd-go-make-sure-CC-and-CXX-are-absolute.patch \
    file://0001-exec.go-do-not-write-linker-flags-into-buildids.patch \
    file://0001-src-cmd-dist-buildgo.go-do-not-hardcode-host-compile.patch \
"
SRC_URI[main.sha256sum] = "2effcd898140da79a061f3784ca4f8d8b13d811fb2abe9dad2404442dabbdf7a"

# Upstream don't believe it is a signifiant real world issue and will only
# fix in 1.17 onwards where we can drop this.
# https://github.com/golang/go/issues/30999#issuecomment-910470358
CVE_CHECK_IGNORE += "CVE-2021-29923"
