SUMMARY = "Exiftool"
DESCRIPTION = "ExifTool is a platform-independent Perl library plus a command-line application for reading, writing and editing meta information in a wide variety of files."
HOMEPAGE = "https://exiftool.org/"
SECTION = "libs"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://perl-Image-ExifTool.spec;beginline=5;endline=5;md5=ffefffc98dab025cb49489bd4d88ee10"

inherit cpan

SRCREV = "5a760372fadf23effe370ab5e9ca8e2df9448411"
SRC_URI = "git://github.com/exiftool/exiftool;protocol=https;branch=master;tag=${PV}"

RDEPENDS:${PN} = " \
    perl \
    perl-module-list-util \
    perl-module-overload \
    perl-module-file-glob \
    perl-module-scalar-util \
    perl-module-compress-zlib \
"

do_install:append() {
    # Remove reference to TMPDIR [buildpaths]
    sed -i -e 's,${TMPDIR},,g' ${D}${bindir}/exiftool

    # Fix shebang and QA Issue [file-rdeps] to use target /usr/bin/env
    sed -i -e '1s|^#!.*env perl|#!/usr/bin/env perl|' ${D}${bindir}/exiftool
}
