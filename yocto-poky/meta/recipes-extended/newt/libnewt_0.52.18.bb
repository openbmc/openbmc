SUMMARY = "A library for text mode user interfaces"

DESCRIPTION = "Newt is a programming library for color text mode, widget based user \
interfaces.  Newt can be used to add stacked windows, entry widgets, \
checkboxes, radio buttons, labels, plain text fields, scrollbars, \
etc., to text mode user interfaces.  This package also contains the \
shared library needed by programs built with newt, as well as a \
/usr/bin/dialog replacement called whiptail.  Newt is based on the \
slang library."

HOMEPAGE = "https://fedorahosted.org/newt/"
SECTION = "libs"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

# slang needs to be >= 2.2
DEPENDS = "slang popt"

SRC_URI = "https://fedorahosted.org/releases/n/e/newt/newt-${PV}.tar.gz \
           file://remove_slang_include.patch \
           file://fix_SHAREDDIR.patch \
           file://cross_ar.patch \
           file://Makefile.in-Add-tinfo-library-to-the-linking-librari.patch \
           file://pie-flags.patch \
"

SRC_URI[md5sum] = "685721bee1a318570704b19dcf31d268"
SRC_URI[sha256sum] = "771b0e634ede56ae6a6acd910728bb5832ac13ddb0d1d27919d2498dab70c91e"

S = "${WORKDIR}/newt-${PV}"

EXTRA_OECONF = "--without-tcl --without-python"

inherit autotools-brokensep

CLEANBROKEN = "1"

export STAGING_INCDIR
export STAGING_LIBDIR
export CPPFLAGS

export BUILD_SYS
export HOST_SYS

PACKAGES_prepend = "whiptail "

do_configure_prepend() {
    sh autogen.sh
}

do_compile_prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.depend
}

FILES_whiptail = "${bindir}/whiptail"

BBCLASSEXTEND = "native"
