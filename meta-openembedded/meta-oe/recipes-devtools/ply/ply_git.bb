SUMMARY = "Ply: A light-weight dynamic tracer for eBPF"
HOMEPAGE = "https://github.com/iovisor/ply"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "bison-native"

SRC_URI = "git://github.com/iovisor/ply"
SRCREV = "aa5b9ac31307ec1acece818be334ef801c802a12"

S = "${WORKDIR}/git"

inherit autotools-brokensep

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|arm.*|powerpc.*)-linux"
