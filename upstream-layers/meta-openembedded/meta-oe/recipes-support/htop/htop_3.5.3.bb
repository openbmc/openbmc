SUMMARY = "Interactive process viewer"
HOMEPAGE = "https://htop.dev"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "ncurses libnl"

SRC_URI = "git://github.com/htop-dev/htop.git;branch=main;protocol=https;tag=${PV} \
           file://0001-configure.ac-Remove-usr-include-libnl3.patch \
"
SRCREV = "20882b8ef8271fb49ea0eb74bbfd484e9b0a11d8"


inherit autotools pkgconfig

CFLAGS += " -I${STAGING_INCDIR}/libnl3"

PACKAGECONFIG ??= " \
    unicode \
    affinity \
    delayacct \
"
PACKAGECONFIG[unicode] = "--enable-unicode,--disable-unicode"
PACKAGECONFIG[affinity] = "--enable-affinity,--disable-affinity,,,,hwloc"
PACKAGECONFIG[unwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[hwloc] = "--enable-hwloc,--disable-hwloc,hwloc,,,affinity"
PACKAGECONFIG[capabilities] = "--enable-capabilities,--disable-capabilities,libcap"
PACKAGECONFIG[delayacct] = "--enable-delayacct,--disable-delayacct,libnl"
PACKAGECONFIG[sensors] = "--enable-sensors,--disable-sensors,lmsensors,lmsensors-libsensors"

FILES:${PN} += "${datadir}/icons/hicolor/scalable/apps/htop.svg"

RDEPENDS:${PN} += "ncurses-terminfo-base"
