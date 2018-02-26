require go-common.inc

GO_BASEVERSION = "1.9"
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
"
SRC_URI_append_libc-musl = " file://set-external-linker.patch"

SRC_URI[main.md5sum] = "da2d44ea384076efec43ee1f8b7d45d2"
SRC_URI[main.sha256sum] = "a4ab229028ed167ba1986825751463605264e44868362ca8e7accc8be057e993"
