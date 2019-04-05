SUMMARY = "IBM's Software TPM 2.0"

LICENSE = "BSD"
SECTION = "securty/tpm"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=1e023f61454ac828b4aa1bc4293f7d5f"

SRC_URI = "https://sourceforge.net/projects/ibmswtpm2/files/ibmtpm1332.tar.gz"
SRC_URI[md5sum] = "0ab34a655b4e09812d7ada19746af4f9"
SRC_URI[sha256sum] = "8e8193af3d11d9ff6a951dda8cd1f4693cb01934a8ad7876b84e92c6148ab0fd"

DEPENDS = "openssl"

S = "${WORKDIR}/src"

LDFLAGS = "${LDFALGS}"

do_compile () {
   make CC='${CC}'
}

do_install () {
   install -d ${D}/${bindir}
   install -m 0755 tpm_server  ${D}/${bindir}
}
