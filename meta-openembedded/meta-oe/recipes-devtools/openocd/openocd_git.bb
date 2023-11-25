SUMMARY = "Free and Open On-Chip Debugging, In-System Programming and Boundary-Scan Testing"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=599d2d1ee7fc84c0467b3d19801db870"
DEPENDS = "libusb-compat libftdi"
RDEPENDS:${PN} = "libusb1"

SRC_URI = " \
    git://repo.or.cz/openocd.git;protocol=http;name=openocd;branch=master \
    git://repo.or.cz/r/git2cl.git;protocol=http;destsuffix=tools/git2cl;name=git2cl;branch=master \
    git://github.com/msteveb/jimtcl.git;protocol=https;destsuffix=git/jimtcl;name=jimtcl;branch=master \
    git://repo.or.cz/r/libjaylink.git;protocol=http;destsuffix=git/src/jtag/drivers/libjaylink;name=libjaylink;branch=master \
"

SRCREV_FORMAT = "openocd"
SRCREV_openocd = "91bd4313444c5a949ce49d88ab487608df7d6c37"
SRCREV_git2cl = "8373c9f74993e218a08819cbcdbab3f3564bbeba"
SRCREV_jimtcl = "fcbb4499a6b46ef69e7a95da53e30796e20817f0"
SRCREV_libjaylink = "9aa7a5957c07bb6e862fc1a6d3153d109c7407e4"

PV = "0.12+gitr${SRCPV}"
S = "${WORKDIR}/git"

inherit pkgconfig autotools-brokensep gettext

BBCLASSEXTEND += "native nativesdk"

EXTRA_OECONF = "--enable-ftdi --enable-jtag_vpi --enable-buspirate --disable-doxygen-html --disable-werror"

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
