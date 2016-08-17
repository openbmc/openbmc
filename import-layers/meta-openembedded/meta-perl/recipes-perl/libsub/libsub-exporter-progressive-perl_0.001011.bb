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
LIC_FILES_CHKSUM = "file://README;beginline=51;endline=53;md5=a171d2e9f8688a44e5f1b6dcc62029e6"

DEPENDS_${PN} = " perl-module-test-more"

SRC_URI = "${CPAN_MIRROR}/authors/id/F/FR/FREW/Sub-Exporter-Progressive-${PV}.tar.gz"
SRC_URI[md5sum] = "bb50b3ba1538902b197c04818a84230a"
SRC_URI[sha256sum] = "0618c6e69c6c0540c41e7560d51981407a6a0768f1330bef6d6ac3c6f1fa7c06"

S = "${WORKDIR}/Sub-Exporter-Progressive-${PV}"

inherit cpan

RDEPENDS_${PN} += " perl-module-carp \
                    perl-module-list-util \
"

BBCLASSEXTEND = "native"
