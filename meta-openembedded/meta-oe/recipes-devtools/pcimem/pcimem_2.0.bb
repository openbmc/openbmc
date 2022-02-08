SUMMARY = "Simple program to read & write to a pci device from userspace"
HOMEPAGE = "https://github.com/billfarrow/pcimem"
BUGTRACKER = "https://github.com/billfarrow/pcimem/issues"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

COMPATIBLE_HOST = "(x86_64|aarch64|arm)"

SRCREV = "09724edb1783a98da2b7ae53c5aaa87493aabc9b"
SRC_URI = "git://github.com/billfarrow/pcimem.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

do_install() {
    install -D -m 0755 ${B}/pcimem ${D}${bindir}/pcimem
}
