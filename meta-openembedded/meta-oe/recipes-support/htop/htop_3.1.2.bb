SUMMARY = "Interactive process viewer"
HOMEPAGE = "https://htop.dev"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "ncurses"

SRC_URI = "git://github.com/htop-dev/htop.git;branch=main;protocol=https \
           file://0001-Use-pkg-config.patch \
"
SRCREV = "d73783d6dbec4a8d9d20d744bf2b5e6a7073aa46"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

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
