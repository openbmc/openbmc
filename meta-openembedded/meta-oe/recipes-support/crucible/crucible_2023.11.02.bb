SUMMARY = "Utility that provides userspace support for reading and writing to the i.MX fuses"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=201414b6610203caed355323b1ab3116"

GO_IMPORT = "github.com/usbarmory/crucible"
SRC_URI = "git://${GO_IMPORT}.git;protocol=https;branch=master"

GO_INSTALL = "\
    ${GO_IMPORT}/cmd/crucible \
    ${GO_IMPORT}/cmd/habtool \
"
SRCREV = "dec27cd4e0e0db106c0a21d429c04ca8d36bbdd5"

export GOPROXY = "https://proxy.golang.org,direct"
# Workaround for network access issue during compile step.
# This needs to be fixed in the recipes buildsystem so that
# it can be accomplished during do_fetch task.
do_compile[network] = "1"

inherit go-mod
