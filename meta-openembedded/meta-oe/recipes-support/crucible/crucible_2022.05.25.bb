SUMMARY = "Utility that provides userspace support for reading and writing to the i.MX fuses"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=a64ced9463b8c81d08335d41966e0592"

GO_IMPORT = "github.com/usbarmory/crucible"
SRC_URI = "git://${GO_IMPORT}.git;protocol=https;branch=master"

GO_INSTALL = "\
    ${GO_IMPORT}/cmd/crucible \
    ${GO_IMPORT}/cmd/habtool \
"
SRCREV = "837bdb65a20de240f92ea57727385352e06b977e"

# Workaround for network access issue during compile step.
# This needs to be fixed in the recipes buildsystem so that
# it can be accomplished during do_fetch task.
do_compile[network] = "1"

inherit go-mod
