SUMMARY = "Free and Open On-Chip Debugging, In-System Programming and Boundary-Scan Testing"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=599d2d1ee7fc84c0467b3d19801db870"
DEPENDS = "libusb-compat libftdi"
RDEPENDS:${PN} = "libusb1"

MIRRORS += " \
    git://repo.or.cz/openocd.git git://github.com/openocd-org/openocd.git \
"

SRC_URI = " \
    git://repo.or.cz/openocd.git;protocol=http;name=openocd;branch=master \
    git://repo.or.cz/r/git2cl.git;protocol=http;destsuffix=tools/git2cl;name=git2cl;branch=master \
    git://github.com/msteveb/jimtcl.git;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/jimtcl;name=jimtcl;branch=master \
    git://repo.or.cz/r/libjaylink.git;protocol=http;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/src/jtag/drivers/libjaylink;name=libjaylink;branch=master \
    file://0001-tcl-board-ti_-_swd_native.cfg-Add-support-for-direct.patch \
    file://0002-tcl-target-ti_k3.cfg-Add-support-for-direct-memory-a.patch \
"

SRCREV_FORMAT = "openocd"
SRCREV_openocd = "66ea461846a3a4a96687c9287c3f61ae8ce0b775"
SRCREV_git2cl = "8373c9f74993e218a08819cbcdbab3f3564bbeba"
SRCREV_jimtcl = "f160866171457474f7c4d6ccda70f9b77524407e"
SRCREV_libjaylink = "0d23921a05d5d427332a142d154c213d0c306eb1"

PV = "0.12+git"

inherit pkgconfig autotools-brokensep gettext

BBCLASSEXTEND += "native nativesdk"

EXTRA_OECONF = "--enable-ftdi --enable-jtag_vpi --enable-buspirate --disable-doxygen-html --disable-werror --enable-internal-jimtcl"

do_configure() {
    ./bootstrap nosubmodule
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/jimtcl/autosetup
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/jimtcl/autosetup
    oe_runconf ${EXTRA_OECONF}
}

do_install() {
    oe_runmake DESTDIR=${D} install
    if [ -e "${D}${infodir}" ]; then
      rm -Rf ${D}${infodir}
    fi
    if [ -e "${D}${mandir}" ]; then
      rm -Rf ${D}${mandir}
    fi
    if [ -e "${D}${bindir}/.debug" ]; then
      rm -Rf ${D}${bindir}/.debug
    fi
}

FILES:${PN} = " \
  ${datadir}/openocd/* \
  ${bindir}/openocd \
  "

PACKAGECONFIG[sysfsgpio] = "--enable-sysfsgpio,--disable-sysfsgpio"
PACKAGECONFIG[remote-bitbang] = "--enable-remote-bitbang,--disable-remote-bitbang"
PACKAGECONFIG ??= "sysfsgpio remote-bitbang"

# Can't be built with ccache
CCACHE_DISABLE = "1"
