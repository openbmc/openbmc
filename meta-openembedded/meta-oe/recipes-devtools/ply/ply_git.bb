SUMMARY = "Ply: A light-weight dynamic tracer for eBPF"
HOMEPAGE = "https://github.com/iovisor/ply"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "bison-native"

PV = "2.3.0"

SRC_URI = "git://github.com/iovisor/ply;branch=master;protocol=https"
SRCREV = "864fac79116870df1ab7aa21e639578807e41e75"

S = "${WORKDIR}/git"

CACHED_CONFIGUREVARS = 'LD="${HOST_PREFIX}ld.bfd${TOOLCHAIN_OPTIONS} ${HOST_LD_ARCH}"'

inherit autotools-brokensep

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|arm.*|powerpc)-linux"
