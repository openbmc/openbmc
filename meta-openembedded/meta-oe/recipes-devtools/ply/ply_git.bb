SUMMARY = "Ply: A light-weight dynamic tracer for eBPF"
HOMEPAGE = "https://github.com/iovisor/ply"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "bison-native"

PV = "2.1.1+git${SRCPV}"

SRC_URI = "git://github.com/iovisor/ply"
SRCREV = "e25c9134b856cc7ffe9f562ff95caf9487d16b59"

S = "${WORKDIR}/git"

inherit autotools-brokensep

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|arm.*|powerpc)-linux"
