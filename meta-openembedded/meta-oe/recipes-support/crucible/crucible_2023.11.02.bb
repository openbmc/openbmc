SUMMARY = "Utility that provides userspace support for reading and writing to the i.MX fuses"

LICENSE = "BSD-3-Clause & ${GO_MOD_LICENSES}"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=201414b6610203caed355323b1ab3116"
require ${BPN}-licenses.inc

SRC_URI = "git://${GO_IMPORT}.git;protocol=https;branch=master;destsuffix=${GO_SRCURI_DESTSUFFIX}"
require ${BPN}-go-mods.inc

SRCREV = "dec27cd4e0e0db106c0a21d429c04ca8d36bbdd5"

S = "${WORKDIR}/git"

GO_IMPORT = "github.com/usbarmory/crucible"
GO_INSTALL = "\
    ${GO_IMPORT}/cmd/crucible \
    ${GO_IMPORT}/cmd/habtool \
"

inherit go-mod
