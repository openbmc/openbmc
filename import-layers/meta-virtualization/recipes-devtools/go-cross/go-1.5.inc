require go-common.inc

PV = "1.5.2"
GO_BASEVERSION = "1.5"
FILESEXTRAPATHS_prepend := "${FILE_DIRNAME}/go-${GO_BASEVERSION}:"


SRC_URI += "\
       file://armhf-elf-header.patch \
       file://syslog.patch \
       file://fix-target-cc-for-build.patch \
       file://fix-cc-handling.patch \
       file://split-host-and-target-build.patch \
       file://gotooldir.patch \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=591778525c869cdde0ab5a1bf283cd81"
SRC_URI[md5sum] = "38fed22e7b80672291e7cba7fb9c3475"
SRC_URI[sha256sum] = "f3ddd624c00461641ce3d3a8d8e3c622392384ca7699e901b370a4eac5987a74"
