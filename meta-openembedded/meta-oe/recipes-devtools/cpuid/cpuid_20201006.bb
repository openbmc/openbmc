SUMMARY = "Linux tool to dump x86 CPUID information about the CPU(s)"
DESCRIPTION = "cpuid dumps detailed information about the CPU(s) gathered \
from the CPUID instruction, and also determines the exact model of CPU(s). \
It supports Intel, AMD, and VIA CPUs, as well as older Transmeta, Cyrix, \
UMC, NexGen, Rise, and SiS CPUs"
HOMEPAGE="http://www.etallen.com/cpuid.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.etallen.com/${BPN}/${BP}.src.tar.gz \
           "
SRC_URI[sha256sum] = "13c53f16408341d11ed489b352f218ec4dbb5bedb796a9d059e924770a9c56a6"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

inherit perlnative

# The install rule from the Makefile has hardcoded paths, so we duplicate
# the actions to accommodate different paths.
do_install () {
    install -D -m 0755 ${B}/cpuid ${D}/${bindir}/cpuid
    install -D -m 0444 ${B}/cpuid.man.gz ${D}/${mandir}
}
