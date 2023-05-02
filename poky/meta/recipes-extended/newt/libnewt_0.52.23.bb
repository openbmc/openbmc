SUMMARY = "A library for text mode user interfaces"

DESCRIPTION = "Newt is a programming library for color text mode, widget based user \
interfaces.  Newt can be used to add stacked windows, entry widgets, \
checkboxes, radio buttons, labels, plain text fields, scrollbars, \
etc., to text mode user interfaces.  This package also contains the \
shared library needed by programs built with newt, as well as a \
/usr/bin/dialog replacement called whiptail.  Newt is based on the \
slang library."

HOMEPAGE = "https://releases.pagure.org/newt/"
SECTION = "libs"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

# slang needs to be >= 2.2
DEPENDS = "slang popt python3"

SRC_URI = "https://releases.pagure.org/newt/newt-${PV}.tar.gz \
           file://cross_ar.patch \
           file://Makefile.in-Add-tinfo-library-to-the-linking-librari.patch \
           file://0001-detect-gold-as-GNU-linker-too.patch \
           "

SRC_URI[sha256sum] = "caa372907b14ececfe298f0d512a62f41d33b290610244a58aed07bbc5ada12a"

S = "${WORKDIR}/newt-${PV}"

inherit autotools-brokensep python3native python3-dir python3targetconfig

EXTRA_OECONF = "--without-tcl --with-python"

EXTRA_OEMAKE += "PYTHONVERS=${PYTHON_DIR}"

CLEANBROKEN = "1"

export CPPFLAGS

PACKAGES:prepend = "whiptail ${PN}-python "

RDEPENDS:${PN}-python += "python3-core"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*"

do_configure:prepend() {
    sh autogen.sh
}

do_compile:prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.depend
}

FILES:whiptail = "${bindir}/whiptail"

BBCLASSEXTEND = "native nativesdk"
