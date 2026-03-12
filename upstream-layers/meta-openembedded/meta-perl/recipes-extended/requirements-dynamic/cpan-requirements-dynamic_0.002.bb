SUMMARY = "Dynamic prerequisites in meta files"
DESCRIPTION = "This module implements a format for describing dynamic prerequisites \
of a distribution.cts and (by default) mails MIME messages."
HOMEPAGE = "https://metacpan.org/pod/CPAN::Requirements::Dynamic"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=818dfe6ef2e2204af6c824ca00e2b485"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/CPAN-Requirements-Dynamic-${PV}.tar.gz"
SRC_URI[sha256sum] = "9e290179fd1ab8574f7a2297baf015ea4fef3703a99d48798f61ec9347b4905b"

S = "${UNPACKDIR}/CPAN-Requirements-Dynamic-${PV}"

inherit cpan
