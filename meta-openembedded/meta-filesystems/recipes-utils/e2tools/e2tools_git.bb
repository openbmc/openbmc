SUMMARY = "Set of GPL'ed utilities to ext2/ext3 filesystem."
DESCRIPTION = "e2tools is a simple set of GPL'ed utilities to read, write, \
and manipulate files in an ext2/ext3 filesystem. These utilities access a \
filesystem directly using the ext2fs library. Can also be used on a Linux \
machine to read/write to disk images or floppies without having to mount \
them or have root access."
HOMEPAGE = "https://github.com/e2tools/e2tools"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "e2fsprogs"

PV = "0.1.0+git"

SRC_URI = " \
           git://github.com/e2tools/e2tools;protocol=https;branch=master \
"
SRCREV = "fd092754a6b65c3a769f74f888668c066f09c36d"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
