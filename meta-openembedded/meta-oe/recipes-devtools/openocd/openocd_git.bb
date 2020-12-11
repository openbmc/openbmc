SUMMARY = "Free and Open On-Chip Debugging, In-System Programming and Boundary-Scan Testing"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libusb-compat libftdi"
RDEPENDS_${PN} = "libusb1"

SRC_URI = " \
    git://repo.or.cz/openocd.git;protocol=http;name=openocd \
    git://repo.or.cz/r/git2cl.git;protocol=http;destsuffix=tools/git2cl;name=git2cl \
    git://repo.or.cz/r/jimtcl.git;protocol=http;destsuffix=git/jimtcl;name=jimtcl \
    git://repo.or.cz/r/libjaylink.git;protocol=http;destsuffix=git/src/jtag/drivers/libjaylink;name=libjaylink \
"

SRCREV_FORMAT = "openocd"
SRCREV_openocd = "d46f28c2ea2611f5fbbc679a5eed253d3dcd2fe3"
SRCREV_git2cl = "8373c9f74993e218a08819cbcdbab3f3564bbeba"
SRCREV_jimtcl = "0aa0fb4e3a38d38a49de9eb585d93d63a370dcf6"
SRCREV_libjaylink = "9aa7a5957c07bb6e862fc1a6d3153d109c7407e4"

PV = "0.10+gitr${SRCPV}"
S = "${WORKDIR}/git"

inherit pkgconfig autotools-brokensep gettext

BBCLASSEXTEND += "native nativesdk"

EXTRA_OECONF = "--enable-ftdi --disable-doxygen-html --disable-werror"

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

FILES_${PN} = " \
  ${datadir}/openocd/* \
  ${bindir}/openocd \
  "

PACKAGECONFIG[sysfsgpio] = "--enable-sysfsgpio,--disable-sysfsgpio"
PACKAGECONFIG[remote-bitbang] = "--enable-remote-bitbang,--disable-remote-bitbang"
PACKAGECONFIG ??= "sysfsgpio remote-bitbang"

# Can't be built with ccache
CCACHE_DISABLE = "1"
