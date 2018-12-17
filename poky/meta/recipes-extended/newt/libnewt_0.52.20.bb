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

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

# slang needs to be >= 2.2
DEPENDS = "slang popt"

SRC_URI = "https://releases.pagure.org/newt/newt-${PV}.tar.gz \
           file://fix_SHAREDDIR.patch \
           file://cross_ar.patch \
           file://Makefile.in-Add-tinfo-library-to-the-linking-librari.patch \
           file://pie-flags.patch \
           file://0001-detect-gold-as-GNU-linker-too.patch \
"

SRC_URI[md5sum] = "70b288f821234593a8e7920e435b259b"
SRC_URI[sha256sum] = "8d66ba6beffc3f786d4ccfee9d2b43d93484680ef8db9397a4fb70b5adbb6dbc"

S = "${WORKDIR}/newt-${PV}"

EXTRA_OECONF = "--without-tcl --without-python"

inherit autotools-brokensep

CLEANBROKEN = "1"

export CPPFLAGS

PACKAGES_prepend = "whiptail "

do_configure_prepend() {
    sh autogen.sh
}

do_compile_prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.depend
}

FILES_whiptail = "${bindir}/whiptail"

BBCLASSEXTEND = "native nativesdk"
