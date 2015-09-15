SUMMARY = "OpenPrinting printer support - filters"
DESCRIPTION = "Foomatic is a printer database designed to make it easier to set up \
common printers for use with UNIX-like operating systems.\
It provides the "glue" between a print spooler (like CUPS or lpr) and \
the printer, by processing files sent to the printer. \
 \
This package consists of filters used by the printer spoolers \
to convert the incoming PostScript data into the printer's native \
format using a printer-specific, but spooler-independent PPD file. \
"

DEPENDS += "cups perl libxml2"
PR = "r1"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${WORKDIR}/foomatic-filters-${PV}/COPYING;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "http://www.openprinting.org/download/foomatic/foomatic-filters-${PV}.tar.gz"

SRC_URI[md5sum] = "b05f5dcbfe359f198eef3df5b283d896"
SRC_URI[sha256sum] = "a2e2e53e502571e88eeb9010c45a0d54671f15707ee104f5c9c22b59ea7a33e3"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-file-converter-check --with-file-converter=texttops"

do_configure_prepend() {
    export PERL="${bindir}/env perl"
    export CUPS_SERVERBIN=${exec_prefix}/lib/cups       # /usr/lib NOT libdir
}

do_install_append_linuxstdbase() {
    install -d ${D}${exec_prefix}/lib/cups/filter
    ln -sf ${bindir}/foomatic-rip ${D}${exec_prefix}/lib/cups/filter
}

FILES_${PN} += "${exec_prefix}/lib/cups/ ${exec_prefix}/lib/ppr/"
