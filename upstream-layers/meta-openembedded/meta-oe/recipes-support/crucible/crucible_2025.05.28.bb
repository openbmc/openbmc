SUMMARY = "Utility that provides userspace support for reading and writing to the i.MX fuses"

LICENSE = "BSD-3-Clause & ${GO_MOD_LICENSES}"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=201414b6610203caed355323b1ab3116"
require ${BPN}-licenses.inc

SRC_URI = "git://${GO_IMPORT}.git;protocol=https;branch=master;destsuffix=${GO_SRCURI_DESTSUFFIX}"
require ${BPN}-go-mods.inc

SRCREV = "9f6ece1a689bca0abbeae230fa05a749fd8086ec"


GO_IMPORT = "github.com/usbarmory/crucible"
GO_INSTALL = "\
    ${GO_IMPORT}/cmd/crucible \
    ${GO_IMPORT}/cmd/habtool \
"

inherit go-mod go-mod-update-modules
