SUMMARY = "Linux tool to dump x86 CPUID information about the CPU(s)"
DESCRIPTION = "cpuid dumps detailed information about the CPU(s) gathered \
from the CPUID instruction, and also determines the exact model of CPU(s). \
It supports Intel, AMD, and VIA CPUs, as well as older Transmeta, Cyrix, \
UMC, NexGen, Rise, and SiS CPUs"
HOMEPAGE="http://www.etallen.com/cpuid.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.etallen.com/${BPN}/${BP}.src.tar.gz \
           "
SRC_URI[sha256sum] = "b1c83045efc26076307751e0662d580277f5f9bf89cf027231a7812003c3a4e8"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

inherit perlnative

# The install rule from the Makefile has hardcoded paths, so we duplicate
# the actions to accommodate different paths.
do_install () {
    install -D -m 0755 ${B}/cpuid ${D}/${bindir}/cpuid
    install -D -m 0444 ${B}/cpuid.man.gz ${D}/${mandir}
}
