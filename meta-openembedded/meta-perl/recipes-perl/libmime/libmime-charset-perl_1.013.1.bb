SUMMARY = "MIME::Charset - Charset Information for MIME."
DESCRIPTION = "MIME::Charset provides information about character sets used for MIME \
messages on Internet."
HOMEPAGE = "https://metacpan.org/release/NEZUMI/MIME-Charset-1.013.1"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEZUMI/MIME-Charset-${PV}.tar.gz"

SRC_URI[sha256sum] = "1bb7a6e0c0d251f23d6e60bf84c9adefc5b74eec58475bfee4d39107e60870f0"

S = "${WORKDIR}/MIME-Charset-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
