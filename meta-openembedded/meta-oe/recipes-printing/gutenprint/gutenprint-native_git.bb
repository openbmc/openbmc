# Recipe for building gutenprint-native
#
# Native part is for generating strings - xmli18n-tmp.h used by target recipe
# We don't depend on cups as this is for generating the string header.
#
# Copyright (c) Ambu A/S - All rights reserved
# SPDX-License-Identifier: MIT
#
# Author(s)
#   clst@ambu.com (Claus Stovgaard)
#

require gutenprint.inc

# We could consider switching to BBCLASSEXTEND native instead of inherit
# Using devtool with this results in warning, as devtool append externalsrc to
# inheritance, making native not being inherited last.
inherit autotools-brokensep pkgconfig native

SECTION = "libs"

# binutils is for xz etc.
# gettext-native for configuration
# flex-native is used for compilation
# byacc-native is for yacc command (compilation)
DEPENDS += "binutils-native gettext-native flex-native byacc-native"

EXTRA_OECONF = "--without-doc --without-cups"

# gutenprint does not have a configure file in its root folder.
# Rather it has a autogen.sh in its root folder. We just use it
do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

# Currently we only uses the string header, even though we compile the complete
# native version of the library. So we limit the install to the needed.
do_install() {
    install -d ${D}${datadir}/gutenprint/
    install -m644 ${B}/src/xml/xmli18n-tmp.h ${D}${datadir}/gutenprint/
}