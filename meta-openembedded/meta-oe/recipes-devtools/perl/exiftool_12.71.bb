SUMMARY = "Exiftool"
DESCRIPTION = "ExifTool is a platform-independent Perl library plus a command-line application for reading, writing and editing meta information in a wide variety of files."
HOMEPAGE = "https://exiftool.org/"
SECTION = "libs"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://perl-Image-ExifTool.spec;beginline=5;endline=5;md5=ffefffc98dab025cb49489bd4d88ee10"

inherit cpan

SRCREV = "7cefbaaa9057231b6e9b5765934f06e75994420d"
SRC_URI = "git://github.com/exiftool/exiftool;protocol=https;branch=master"

S = "${WORKDIR}/git"

RDEPENDS:${PN} = " \
    perl \
    perl-module-list-util \
    perl-module-overload \
    perl-module-file-glob \
    perl-module-scalar-util \
    perl-module-compress-zlib \
"
