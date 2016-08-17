SUMMARY = "Text::Diff - Perform diffs on files and record sets"
DESCRIPTION = "diff() provides a basic set of services akin to the GNU diff \
utility. It is not anywhere near as feature complete as GNU diff, but it is \
better integrated with Perl and available on all platforms. It is often \
faster than shelling out to a system's diff executable for small files, \
and generally slower on larger files."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~ovid/Text-Diff/"

LICENSE = "Artistic-1.0 | GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

SRC_URI = "${CPAN_MIRROR}/authors/id/O/OV/OVID/Text-Diff-${PV}.tar.gz"
SRC_URI[md5sum] = "30d56e6dd5551ca16b8e16cc7299dc21"
SRC_URI[sha256sum] = "a67f50a48e313c1680cc662109ce5f913ea71454db355d0cf4db87ac89d2d2fa"

S = "${WORKDIR}/Text-Diff-${PV}"

inherit cpan

RDEPENDS_${PN} = " libalgorithm-diff-perl \
                   perl-module-extutils-makemaker \
                   perl-module-exporter \
"

BBCLASSEXTEND = "native"
