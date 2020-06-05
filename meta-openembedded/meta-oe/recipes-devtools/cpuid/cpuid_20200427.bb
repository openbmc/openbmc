SUMMARY = "Linux tool to dump x86 CPUID information about the CPU(s)"
DESCRIPTION = "cpuid dumps detailed information about the CPU(s) gathered \
from the CPUID instruction, and also determines the exact model of CPU(s). \
It supports Intel, AMD, and VIA CPUs, as well as older Transmeta, Cyrix, \
UMC, NexGen, Rise, and SiS CPUs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.etallen.com/${BPN}/${BP}.src.tar.gz \
           "
SRC_URI[md5sum] = "daa93ee2fdaf461e515b4713e7337076"
SRC_URI[sha256sum] = "c1a9463f1f2175086120e9079e318bbd383dc1e574fafb2f724879f1d11061d4"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

inherit perlnative

# The install rule from the Makefile has hardcoded paths, so we duplicate
# the actions to accommodate different paths.
do_install () {
    install -D -m 0755 ${B}/cpuid ${D}/${bindir}/cpuid
    install -D -m 0444 ${B}/cpuid.man.gz ${D}/${mandir}
}
