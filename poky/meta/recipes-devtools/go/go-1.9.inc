require go-common.inc

GO_BASEVERSION = "1.9"
GO_MINOR = ".7"
PV .= "${GO_MINOR}"

FILESEXTRAPATHS_prepend := "${FILE_DIRNAME}/go-${GO_BASEVERSION}:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
        file://0001-make.bash-quote-CC_FOR_TARGET.patch \
        file://0002-cmd-go-fix-CC-and-CXX-environment-variable-construct.patch \
        file://0003-make.bash-better-separate-host-and-target-builds.patch \
        file://0004-cmd-go-allow-GOTOOLDIR-to-be-overridden-in-the-envir.patch \
        file://0005-cmd-go-make-GOROOT-precious-by-default.patch \
        file://0006-make.bash-add-GOTOOLDIR_BOOTSTRAP-environment-variab.patch \
        file://0007-ld-add-soname-to-shareable-objects.patch \
        file://0008-make.bash-add-GOHOSTxx-indirection-for-cross-canadia.patch \
        file://0009-cmd-go-buildmode-pie-forces-external-linking-mode-on.patch \
        file://0010-make.bash-override-CC-when-building-dist-and-go_boot.patch \
        file://0011-use-GOBUILDMODE-to-set-buildmode.patch \
"
SRC_URI_append_libc-musl = " file://set-external-linker.patch"

SRC_URI[main.md5sum] = "3c2cf876ed6612a022574a565206c6ea"
SRC_URI[main.sha256sum] = "582814fa45e8ecb0859a208e517b48aa0ad951e3b36c7fff203d834e0ef27722"
