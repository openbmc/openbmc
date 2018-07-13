SUMMARY = "The TPM Quote Tools is a collection of programs that provide support \
  for TPM based attestation using the TPM quote mechanism. \
  "
DESCRIPTION = "The TPM Quote Tools is a collection of programs that provide support \
  for TPM based attestation using the TPM quote mechanism.  The manual \
  page for tpm_quote_tools provides a usage overview. \
  \
  TPM Quote Tools has been tested with TrouSerS on Linux and NTRU on \
  Windows XP.  It was ported to Windows using MinGW and MSYS. \
  "
HOMEPAGE = "https://sourceforge.net/projects/tpmquotetools/"
SECTION = "security/tpm"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ec30b01163d242ecf07d9cd84e3611f"

DEPENDS = "libtspi tpm-tools"

SRC_URI = "${SOURCEFORGE_MIRROR}/tpmquotetools/${PV}/${BP}.tar.gz"

SRC_URI[md5sum] = "6e194f5bc534301bbaef53dc6d22c233"
SRC_URI[sha256sum] = "10dc4eade02635557a9496b388360844cd18e7864e2eb882f5e45ab2fa405ae2"

inherit autotools
