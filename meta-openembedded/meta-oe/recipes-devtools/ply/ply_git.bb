SUMMARY = "Ply: A light-weight dynamic tracer for eBPF"
HOMEPAGE = "https://github.com/iovisor/ply"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "bison-native"

PV = "2.4.0"

SRC_URI = "git://github.com/iovisor/ply;branch=master;protocol=https"
SRCREV = "282558cfa1c0f0e095354e3a6ec4486b524179dc"

S = "${WORKDIR}/git"

CACHED_CONFIGUREVARS = 'LD="${HOST_PREFIX}ld.bfd${TOOLCHAIN_OPTIONS} ${HOST_LD_ARCH}"'

inherit autotools-brokensep

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|arm.*|powerpc)-linux"
