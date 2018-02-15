SUMMARY = "YAML::Tiny Version 1.69"
PR = "r1"
LICENSE = "Artistic-1.0 | GPL-2.0"
HOMEPAGE = "https://metacpan.org/release/YAML-Tiny"

inherit native
inherit cpan

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI += "https://cpan.metacpan.org/authors/id/E/ET/ETHER/YAML-Tiny-1.69.tar.gz"

SRC_URI[md5sum] = "7deacd0ee428038407ccc5cd0b50f66f"
SRC_URI[sha256sum] = "bc8cb059492b9e4f7be1bcefd99dfa834b13438d48fd465c1e312223f584f592"

S = "${WORKDIR}/YAML-Tiny-${PV}"
