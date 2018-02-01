require go-common.inc

PV = "1.7.4"
GO_BASEVERSION = "1.7"
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
SRC_URI[md5sum] = "49c1076428a5d3b5ad7ac65233fcca2f"
SRC_URI[sha256sum] = "4c189111e9ba651a2bb3ee868aa881fab36b2f2da3409e80885ca758a6b614cc"

