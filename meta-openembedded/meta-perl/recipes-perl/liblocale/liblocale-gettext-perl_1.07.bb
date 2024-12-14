SUMMARY = "Locale::gettext - message handling functions."
DESCRIPTION = "The gettext module permits access from perl to the gettext() family of \
functions for retrieving message strings from databases constructed to \
internationalize software."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Locale::gettext"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;md5=d028249c2d08dca6ca6c5bb43b56d926"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PV/PVANDRY/Locale-gettext-${PV}.tar.gz"

SRC_URI[sha256sum] = "909d47954697e7c04218f972915b787bd1244d75e3bd01620bc167d5bbc49c15"

S = "${WORKDIR}/Locale-gettext-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
