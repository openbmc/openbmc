SUMMARY = "Free and Open On-Chip Debugging, In-System Programming and Boundary-Scan Testing"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libusb-compat libftdi"
RDEPENDS_${PN} = "libusb1"

SRC_URI = "git://repo.or.cz/openocd.git \
           file://0001-Add-fallthrough-comments.patch \
           file://0002-Workaround-new-warnings-generated-by-GCC-7.patch \
           file://0003-armv7a-Add-missing-break-to-fix-fallthrough-warning.patch \
           file://0004-Fix-overflow-warning.patch \
           file://0005-command-Move-the-fall-through-comment-to-right-scope.patch \
"
SRCREV = "1025be363e2bf42f1613083223a2322cc3a9bd4c"

PV = "0.10+gitr${SRCPV}"
S = "${WORKDIR}/git"

inherit pkgconfig autotools-brokensep gettext

BBCLASSEXTEND += "nativesdk"

EXTRA_OECONF = "--enable-ftdi --disable-doxygen-html "

do_configure() {
    ./bootstrap
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
PACKAGECONFIG ??= "sysfsgpio"
