require go-common.inc

GOMINOR = ""
GO_BASEVERSION = "1.8"
PV .= "${GOMINOR}"
FILESEXTRAPATHS_prepend := "${FILE_DIRNAME}/go-${GO_BASEVERSION}:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
       file://armhf-elf-header.patch \
       file://syslog.patch \
       file://fix-target-cc-for-build.patch \
       file://fix-cc-handling.patch \
       file://split-host-and-target-build.patch \
       file://gotooldir.patch \
"
SRC_URI[md5sum] = "7743960c968760437b6e39093cfe6f67"
SRC_URI[sha256sum] = "406865f587b44be7092f206d73fc1de252600b79b3cacc587b74b5ef5c623596"
