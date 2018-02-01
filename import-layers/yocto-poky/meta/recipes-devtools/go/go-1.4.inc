require go-common.inc

PV = "1.4.3"
GO_BASEVERSION = "1.4"
FILESEXTRAPATHS_prepend := "${FILE_DIRNAME}/go-${GO_BASEVERSION}:"

SRC_URI += "\
        file://016-armhf-elf-header.patch \
        file://go-cross-backport-cmd-link-support-new-386-amd64-rel.patch \
        file://syslog.patch \
        file://0001-cmd-ld-set-alignment-for-the-.rel.plt-section-on-32-.patch \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=591778525c869cdde0ab5a1bf283cd81"
SRC_URI[md5sum] = "dfb604511115dd402a77a553a5923a04"
SRC_URI[sha256sum] = "9947fc705b0b841b5938c48b22dc33e9647ec0752bae66e50278df4f23f64959"
