SUMMARY = "Sub::Exporter::Progressive - Only use Sub::Exporter if you need it."
DESCRIPTION = "\"Sub::Exporter\" is an incredibly powerful module, but with \
that power comes great responsibility, er- as well as some runtime penalties. \
This module is a \"Sub::Exporter\" wrapper that will let your users just use \
\"Exporter\" if all they are doing is picking exports, but use \
\"Sub::Exporter\" if your users try to use \"Sub::Exporter's\" more advanced \
features, like renaming exports, if they try to use them."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Sub-Exporter-Progressive/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=003fa970662359a43ac2c2961108b0f1"

DEPENDS_${PN} = " perl-module-test-more"

SRC_URI = "${CPAN_MIRROR}/authors/id/F/FR/FREW/Sub-Exporter-Progressive-${PV}.tar.gz"
SRC_URI[md5sum] = "72cf6acdd2a0a8b105821a4db98e4ebe"
SRC_URI[sha256sum] = "d535b7954d64da1ac1305b1fadf98202769e3599376854b2ced90c382beac056"

S = "${WORKDIR}/Sub-Exporter-Progressive-${PV}"

inherit cpan

RDEPENDS_${PN} += " perl-module-carp \
                    perl-module-list-util \
"

BBCLASSEXTEND = "native"
