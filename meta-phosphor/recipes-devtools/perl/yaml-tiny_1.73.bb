SUMMARY = "YAML::Tiny Version 1.73"
PR = "r1"
LICENSE = "Artistic-1.0 | GPL-2.0"
HOMEPAGE = "https://metacpan.org/release/YAML-Tiny"

inherit cpan
inherit allarch

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI += "https://cpan.metacpan.org/authors/id/E/ET/ETHER/YAML-Tiny-1.73.tar.gz"

SRC_URI[md5sum] = "d1bb2525e4ab46bfab4b22842c467529"
SRC_URI[sha256sum] = "bc315fa12e8f1e3ee5e2f430d90b708a5dc7e47c867dba8dce3a6b8fbe257744"

S = "${WORKDIR}/YAML-Tiny-${PV}"

BBCLASSEXTEND = "native"
