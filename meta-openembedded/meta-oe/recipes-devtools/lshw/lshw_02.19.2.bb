DESCRIPTION = "A small tool to provide detailed information on the hardware \
configuration of the machine. It can report exact memory configuration, \
firmware version, mainboard configuration, CPU version and speed, cache \
configuration, bus speed, etc. on DMI-capable or EFI systems."
SUMMARY = "Hardware lister"
HOMEPAGE = "http://ezix.org/project/wiki/HardwareLiSter"
SECTION = "console/tools"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRC_URI = " \
    http://ezix.org/software/files/lshw-B.${PV}.tar.gz \
    file://0001-Fix-musl-build.patch \
"
SRC_URI[md5sum] = "8c70d46e906688309095c73ecb9396e3"
SRC_URI[sha256sum] = "9bb347ac87142339a366a1759ac845e3dbb337ec000aa1b99b50ac6758a80f80"

S = "${WORKDIR}/lshw-B.${PV}"

do_compile() {
    # build core only - don't ship gui
    oe_runmake -C src core
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native"
