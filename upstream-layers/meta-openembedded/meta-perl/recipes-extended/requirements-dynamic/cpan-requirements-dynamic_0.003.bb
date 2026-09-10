SUMMARY = "Dynamic prerequisites in meta files"
DESCRIPTION = "This module implements a format for describing dynamic prerequisites \
of a distribution.cts and (by default) mails MIME messages."
HOMEPAGE = "https://metacpan.org/pod/CPAN::Requirements::Dynamic"
LICENSE = "Artistic-1.0 OR GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1bae8e4882b2dc386a0b72b7265c4a22"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/CPAN-Requirements-Dynamic-${PV}.tar.gz"
SRC_URI[sha256sum] = "0731694542ec57aead9f20259ba678f74e5602e655b1833eef3e9336f47298e8"

S = "${UNPACKDIR}/CPAN-Requirements-Dynamic-${PV}"

inherit cpan
