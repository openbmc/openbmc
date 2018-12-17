SUMMARY = "Linux tool to dump x86 CPUID information about the CPU(s)"
DESCRIPTION = "cpuid dumps detailed information about the CPU(s) gathered \
from the CPUID instruction, and also determines the exact model of CPU(s). \
It supports Intel, AMD, and VIA CPUs, as well as older Transmeta, Cyrix, \
UMC, NexGen, Rise, and SiS CPUs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.etallen.com/${BPN}/${BP}.src.tar.gz \
           "
SRC_URI[md5sum] = "b3b4e44ef49575043a91def0207dcc76"
SRC_URI[sha256sum] = "967823be36f23cbc972eb0aa882d069c1d155a5978990ac3bcf425e6a2e7ff9a"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

# The install rule from the Makefile has hardcoded paths, so we duplicate
# the actions to accommodate different paths.
do_install () {
    install -D -m 0755 ${B}/cpuid ${D}/${bindir}/cpuid
    install -D -m 0444 ${B}/cpuid.man.gz ${D}/${mandir}
}
