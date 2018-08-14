SUMMARY = "Linux tool to dump x86 CPUID information about the CPU(s)"
DESCRIPTION = "cpuid dumps detailed information about the CPU(s) gathered \
from the CPUID instruction, and also determines the exact model of CPU(s). \
It supports Intel, AMD, and VIA CPUs, as well as older Transmeta, Cyrix, \
UMC, NexGen, Rise, and SiS CPUs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.etallen.com/${BPN}/${BP}.src.tar.gz"
SRC_URI[md5sum] = "1c46a6662626c5a6eaca626f23a5a7d7"
SRC_URI[sha256sum] = "667612aae6704341dd10844e97c84c5c5c8700817a5937a3c293b55013bc4865"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

# The install rule from the Makefile has hardcoded paths, so we duplicate
# the actions to accommodate different paths.
do_install () {
    install -d -m755 ${D}/${bindir}
    install -m755 ${B}/cpuid ${D}/${bindir}/cpuid
    install -d -m755 ${D}/${mandir}
    install -m444 ${B}/cpuid.man.gz ${D}/${mandir}
}
