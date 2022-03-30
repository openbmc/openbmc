SUMMARY = "Function returning \"${^GLOBAL_PHASE} eq \'DESTRUCT\'\""
DESCRIPTION = "Perl's global destruction is a little trick to deal with \
WRT finalizers because it's not ordered and objects can sometimes disappear."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Devel-GlobalDestruction/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=53;endline=55;md5=935dadb9423774f53548e5cd5055d41a"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Devel-GlobalDestruction-${PV}.tar.gz"
SRC_URI[md5sum] = "24221ba322cf2dc46a1fc99b53e2380b"
SRC_URI[sha256sum] = "34b8a5f29991311468fe6913cadaba75fd5d2b0b3ee3bb41fe5b53efab9154ab"

S = "${WORKDIR}/Devel-GlobalDestruction-${PV}"

inherit cpan

RDEPENDS:${PN} = " libsub-exporter-progressive-perl \
"

BBCLASSEXTEND = "native"
