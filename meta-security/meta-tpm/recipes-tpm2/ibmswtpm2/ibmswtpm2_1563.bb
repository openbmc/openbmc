SUMMARY = "IBM's Software TPM 2.0"
LICENSE = "BSD"
SECTION = "securty/tpm"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=1e023f61454ac828b4aa1bc4293f7d5f"

DEPENDS = "openssl"

SRC_URI = "https://sourceforge.net/projects/ibmswtpm2/files/ibmtpm${PV}.tar.gz \
           file://remove_optimization.patch \
           "
SRC_URI[md5sum] = "13013612b3a13dc935fefe1a5684179c"
SRC_URI[sha256sum] = "fc3a17f8315c1f47670764f2384943afc0d3ba1e9a0422dacb08d455733bd1e9"
SRC_URI[sha1sum] = "a2a5335024a2edc1739f08b99e716fa355be627d"
SRC_URI[sha384sum] = "b1f278acabe2198aa79c0fe8aa0182733fe701336cbf54a88058be0b574cab768f59f9315882d0e689e634678d05b79f"
SRC_URI[sha512sum] = "ff0b9e5f0d0070eb572b23641f7a0e70a8bc65cbf4b59dca1778be3bb014124011221a492147d4c492584e87af23e2f842ca6307641b3919f67a3f27f09312c0"

S = "${WORKDIR}/src"

do_compile () {
   make CC='${CC}'
}

do_install () {
   install -d ${D}/${bindir}
   install -m 0755 tpm_server  ${D}/${bindir}
}

