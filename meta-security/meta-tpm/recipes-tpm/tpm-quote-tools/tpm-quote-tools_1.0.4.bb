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

SRC_URI = "git://git.code.sf.net/p/tpmquotetools/tpm-quote-tools;branch=master"
SRCREV = "4511874d5c9b4504bb96e94f8a14bd6c39a36295"

S = "${WORKDIR}/git"
inherit autotools
