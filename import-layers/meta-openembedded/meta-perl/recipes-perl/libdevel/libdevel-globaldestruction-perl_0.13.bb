SUMMARY = "Function returning \"${^GLOBAL_PHASE} eq \'DESTRUCT\'\""
DESCRIPTION = "Perl's global destruction is a little trick to deal with \
WRT finalizers because it's not ordered and objects can sometimes disappear."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Devel-GlobalDestruction/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=53;endline=55;md5=935dadb9423774f53548e5cd5055d41a"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Devel-GlobalDestruction-${PV}.tar.gz"
SRC_URI[md5sum] = "e7be00040827e204b2b6cba2f3166074"
SRC_URI[sha256sum] = "b29824dc0d322e56da325f05185367eb443694716010b36693dd52ffbe8ec462"

S = "${WORKDIR}/Devel-GlobalDestruction-${PV}"

inherit cpan

RDEPENDS_${PN} = " libsub-exporter-progressive-perl \
"

BBCLASSEXTEND = "native"
