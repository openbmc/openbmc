require go-common.inc

GO_BASEVERSION = "1.14"
GO_MINOR = ".15"
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
    file://0008-use-GOBUILDMODE-to-set-buildmode.patch \
    file://CVE-2021-34558.patch \
    file://CVE-2021-33196.patch \
    file://CVE-2021-33197.patch \
    file://CVE-2021-38297.patch \
    file://CVE-2022-23806.patch \
    file://CVE-2022-23772.patch \
"

SRC_URI_append_libc-musl = " file://0009-ld-replace-glibc-dynamic-linker-with-musl.patch"
SRC_URI[main.sha256sum] = "7ed13b2209e54a451835997f78035530b331c5b6943cdcd68a3d815fdc009149"

# Upstream don't believe it is a signifiant real world issue and will only
# fix in 1.17 onwards where we can drop this.
# https://github.com/golang/go/issues/30999#issuecomment-910470358
CVE_CHECK_WHITELIST += "CVE-2021-29923"

