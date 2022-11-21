# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- README
# +++ README
# @@ -1,4 +1,4 @@
# -       Copyright (c) 2005-2020 Paul Marquess. All rights reserved.
# +        Copyright (c) 2005-2022 Paul Marquess. All rights reserved.
#            This program is free software; you can redistribute it
#             and/or modify it under the same terms as Perl itself.
#  
# 
#

SUMMARY = "Perl interface to the zlib compression library."
DESCRIPTION = "The Compress::Raw::Zlib module provides a Perl interface \
to the zlib compression library (see 'AUTHOR' for details about where to \
get zlib)."
HOMEPAGE = "https://metacpan.org/release/Compress-Raw-Zlib"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=17;md5=9bd174bdd6fbb141c1b679e2466e0b39"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/Compress-Raw-Zlib-${PV}.tar.gz"

SRC_URI[sha256sum] = "96e20946eb457a32d2d7a0050b922e37b5ada41246bcdc824196d3f7c4da91b7"

DEPENDS += "zlib"

S = "${WORKDIR}/Compress-Raw-Zlib-${PV}"

inherit cpan

export BUILD_ZLIB="0"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
