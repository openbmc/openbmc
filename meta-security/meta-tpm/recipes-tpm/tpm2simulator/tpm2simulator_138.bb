SUMMARY = "TPM 2.0 Simulator Extraction Script"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1415f7be284540b81d9d28c67c1a6b8b"

DEPENDS = "python"

SRCREV = "e45324eba268723d39856111e7933c5c76238481"
SRC_URI = "git://github.com/stwagnr/tpm2simulator.git"

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/cmake"

inherit native lib_package cmake

EXTRA_OECMAKE = " \
	-DCMAKE_BUILD_TYPE=Debug \
	-DSPEC_VERSION=138 \
"

do_configure_prepend () {
	sed -i 's/^SET = False/SET = True/' ${S}/scripts/settings.py 
}
