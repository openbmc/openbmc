DESCRIPTION = "A small tool to provide detailed information on the hardware \
configuration of the machine. It can report exact memory configuration, \
firmware version, mainboard configuration, CPU version and speed, cache \
configuration, bus speed, etc. on DMI-capable or EFI systems."
SUMMARY = "Hardware lister"
HOMEPAGE = "http://ezix.org/project/wiki/HardwareLiSter"
SECTION = "console/tools"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64|riscv64).*-linux"

PV .= "+git${SRCPV}"
SRCREV = "b4e067307906ec6f277cce5c8a882f5edd03cbbc"
SRC_URI = " \
    git://github.com/lyonel/lshw.git;protocol=https;branch=master \
    file://0001-disable-docbook2man.patch \
"

S = "${WORKDIR}/git"

inherit pkgconfig

DEPENDS = "gettext-native"

PACKAGECONFIG ??= "zlib"
PACKAGECONFIG[sqlite] = "SQLITE=1,SQLITE=0,sqlite3"
PACKAGECONFIG[zlib]   = "ZLIB=1,ZLIB=0,zlib gzip-native"

# use the PACKAGECONFIG configurations arguments
EXTRA_OEMAKE = "${PACKAGECONFIG_CONFARGS} RPM_OPT_FLAGS='${CFLAGS}'"

do_compile() {
    # build core only - don't ship gui
    oe_runmake -C src core
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native"
