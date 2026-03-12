SUMMARY = "Interactive process viewer"
HOMEPAGE = "https://htop.dev"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "ncurses libnl"

SRC_URI = "git://github.com/htop-dev/htop.git;branch=main;protocol=https \
           file://0001-configure.ac-Remove-usr-include-libnl3.patch \
"
SRCREV = "348c0a6bf4f33571835a0b6a1a0f5deb15132128"


inherit autotools pkgconfig

CFLAGS += " -I${STAGING_INCDIR}/libnl3"

PACKAGECONFIG ??= " \
    unicode \
    affinity \
    delayacct \
"
PACKAGECONFIG[unicode] = "--enable-unicode,--disable-unicode"
PACKAGECONFIG[affinity] = "--enable-affinity,--disable-affinity,,,,hwloc"
PACKAGECONFIG[unwind] = "--enable-unwind,--disable-unwind,libunwind"
PACKAGECONFIG[hwloc] = "--enable-hwloc,--disable-hwloc,hwloc,,,affinity"
PACKAGECONFIG[openvz] = "--enable-openvz,--disable-openvz"
PACKAGECONFIG[vserver] = "--enable-vserver,--disable-vserver"
PACKAGECONFIG[ancient-vserver] = "--enable-ancient-vserver,--disable-ancient-vserver"
PACKAGECONFIG[capabilities] = "--enable-capabilities,--disable-capabilities,libcap"
PACKAGECONFIG[delayacct] = "--enable-delayacct,--disable-delayacct,libnl"
PACKAGECONFIG[sensors] = "--enable-sensors,--disable-sensors,lmsensors,lmsensors-libsensors"

FILES:${PN} += "${datadir}/icons/hicolor/scalable/apps/htop.svg"

RDEPENDS:${PN} += "ncurses-terminfo-base"
