HOMEPAGE = "https://sourceware.org/systemtap/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://sourceware.org/git/systemtap.git;protocol=https;branch=master;tag=release-${PV}"

SRCREV = "211e84349504077f0c84f759601b02f946465d29"

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|arm|aarch64|microblazeel|mips|riscv64|riscv32).*-linux'
COMPATIBLE_HOST:libc-musl = 'null'
