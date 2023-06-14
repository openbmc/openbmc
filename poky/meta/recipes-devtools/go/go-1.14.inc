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
    file://CVE-2021-44717.patch \
    file://CVE-2022-24675.patch \
    file://CVE-2021-31525.patch \
    file://CVE-2022-30629.patch \
    file://CVE-2022-30631.patch \
    file://CVE-2022-30632.patch \
    file://CVE-2022-30633.patch \
    file://CVE-2022-30635.patch \
    file://CVE-2022-32148.patch \
    file://CVE-2022-32189.patch \
    file://CVE-2021-27918.patch \
    file://CVE-2021-36221.patch \
    file://CVE-2021-39293.patch \
    file://CVE-2021-41771.patch \
    file://CVE-2022-27664.patch \
    file://0001-CVE-2022-32190.patch \
    file://0002-CVE-2022-32190.patch \
    file://0003-CVE-2022-32190.patch \
    file://0004-CVE-2022-32190.patch \
    file://CVE-2022-2880.patch \
    file://CVE-2022-2879.patch \
    file://CVE-2021-33195.patch \
    file://CVE-2021-33198.patch \
    file://CVE-2021-44716.patch \
    file://CVE-2022-24921.patch \
    file://CVE-2022-28131.patch \
    file://CVE-2022-28327.patch \
    file://CVE-2022-41715.patch \
    file://CVE-2022-41717.patch \
    file://CVE-2022-1962.patch \
    file://CVE-2022-41723.patch \
    file://CVE-2022-41722-1.patch \
    file://CVE-2022-41722-2.patch \
    file://CVE-2020-29510.patch \
    file://CVE-2023-24537.patch \
    file://CVE-2023-24534.patch \
    file://CVE-2023-24538-1.patch \
    file://CVE-2023-24538-2.patch \
    file://CVE-2023-24538-3.patch \
    file://CVE-2023-24539.patch \
    file://CVE-2023-24540.patch \
"

SRC_URI_append_libc-musl = " file://0009-ld-replace-glibc-dynamic-linker-with-musl.patch"
SRC_URI[main.sha256sum] = "7ed13b2209e54a451835997f78035530b331c5b6943cdcd68a3d815fdc009149"

# Upstream don't believe it is a signifiant real world issue and will only
# fix in 1.17 onwards where we can drop this.
# https://github.com/golang/go/issues/30999#issuecomment-910470358
CVE_CHECK_WHITELIST += "CVE-2021-29923"

# this issue affected go1.15 onwards
# https://security-tracker.debian.org/tracker/CVE-2022-29526
CVE_CHECK_WHITELIST += "CVE-2022-29526"

# Issue only on windows
CVE_CHECK_WHITELIST += "CVE-2022-29804"
CVE_CHECK_WHITELIST += "CVE-2022-30580"
CVE_CHECK_WHITELIST += "CVE-2022-30634"

# Issue is in golang.org/x/net/html/parse.go, not used in go compiler
CVE_CHECK_WHITELIST += "CVE-2021-33194"

# Issue introduced in go1.16, does not exist in 1.14
CVE_CHECK_WHITELIST += "CVE-2021-41772"

# Fixes code that was added in go1.16, does not exist in 1.14
CVE_CHECK_WHITELIST += "CVE-2022-30630"

# This is specific to Microsoft Windows
CVE_CHECK_WHITELIST += "CVE-2022-41716"

# Issue introduced in go1.15beta1, does not exist in 1.14
CVE_CHECK_WHITELIST += "CVE-2022-1705"
