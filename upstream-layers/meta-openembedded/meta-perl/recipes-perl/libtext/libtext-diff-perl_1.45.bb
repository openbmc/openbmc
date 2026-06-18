SUMMARY = "Text::Diff - Perform diffs on files and record sets"
DESCRIPTION = "diff() provides a basic set of services akin to the GNU diff \
utility. It is not anywhere near as feature complete as GNU diff, but it is \
better integrated with Perl and available on all platforms. It is often \
faster than shelling out to a system's diff executable for small files, \
and generally slower on larger files."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/NEILB/Text-Diff-1.45"

LICENSE = "Artistic-1.0 | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e795454c53c99fe48b8180eb5917b10"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEILB/Text-Diff-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "e8baa07b1b3f53e00af3636898bbf73aec9a0ff38f94536ede1dbe96ef086f04"

S = "${UNPACKDIR}/Text-Diff-${PV}"

inherit cpan ptest
RDEPENDS:${PN}-ptest += "perl-module-test perl-module-test-more"

RDEPENDS:${PN} = " libalgorithm-diff-perl \
                   perl-module-extutils-makemaker \
                   perl-module-exporter \
"

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
