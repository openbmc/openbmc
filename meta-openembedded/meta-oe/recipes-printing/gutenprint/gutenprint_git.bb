# Recipe for building gutenprint
#
# Uses the gutenprint-native extracted strings and disable local build stuff
#
# Copyright (c) Ambu A/S - All rights reserved
# SPDX-License-Identifier: MIT
#
# Author(s)
#   clst@ambu.com (Claus Stovgaard)
#

require gutenprint.inc

inherit autotools-brokensep pkgconfig

DEPENDS += "cups gutenprint-native"

# cups-genppdupdate depend on perl
# We also set the path to PERL as else the version in hosttools would be used
# with full path
RDEPENDS:${PN} = "perl "

EXTRA_OECONF = "--without-doc --disable-test --disable-nls PERL=/usr/bin/perl"

# gutenprint does not have a configure file in its root folder.
# Rather it has a autogen.sh in its root folder. We just use it
# together with adapting for cross compilation
do_configure() {
    # Disable the xmli18n-tmp.h rule
    # It depend on the local build extract-strings, we are not able to run this
    # So we are using the xmli18n-tmp.h created by gutenprint-native
    sed -i 's/all-local: xmli18n-tmp.h xml-stamp/all-local: xml-stamp/'  ${S}/src/xml/Makefile.am
    sed -i 's/dist-hook: xmli18n-tmp.h xml-stamp/dist-hook: xml-stamp/'  ${S}/src/xml/Makefile.am
    cp ${RECIPE_SYSROOT_NATIVE}${datadir}/gutenprint/xmli18n-tmp.h ${S}/src/xml/

    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

# gutenprint install the calibrate.ppm and net.sf.gimp-print.usb-quirks in
# /usr/share/cups
FILES:${PN} += "${datadir}/cups/*"

# Install in /etc/cups when RPM needs DIRFILES to not conflict
# https://stackoverflow.com/questions/44762430/why-do-i-get-etc-cups-conflicts-between-attempted-installs-in-yocto
DIRFILES = "1"