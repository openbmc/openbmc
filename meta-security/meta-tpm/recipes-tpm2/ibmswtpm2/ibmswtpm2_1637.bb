SUMMARY = "IBM's Software TPM 2.0"
DESCRIPTION = "The software TPM 2.0 is targeted toward application development, \
education, and virtualization. \
\
The intent is that an application can be developed using the software TPM. \
The application should then run using a hardware TPM without changes. \
Advantages of this approach: \
* In contrast to a hardware TPM, it runs on many platforms and it's generally faster. \
* Application software errors are easily reversed by simply removing the TPM state and starting over. \
* Difficult crypto errors are quickly debugged by looking inside the TPM."
HOMEPAGE = "http://ibmswtpm.sourceforge.net/ibmswtpm2.html"
LICENSE = "BSD"
SECTION = "securty/tpm"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=1e023f61454ac828b4aa1bc4293f7d5f"

DEPENDS = "openssl"

SRC_URI = "https://sourceforge.net/projects/ibmswtpm2/files/ibmtpm${PV}.tar.gz \
           file://tune-makefile.patch \
           file://fix-wrong-cast.patch \
           "
SRC_URI[md5sum] = "43b217d87056e9155633925eb6ef749c"
SRC_URI[sha256sum] = "dd3a4c3f7724243bc9ebcd5c39bbf87b82c696d1c1241cb8e5883534f6e2e327"
SRC_URI[sha1sum] = "ab4b94079e57a86996991e8a2b749ce063e4ad3e"
SRC_URI[sha384sum] = "bbef16a934853ce78cba7ddc766aa9d7ef3cde3430a322b1be772bf3ad4bd6d413ae9c4de21bc1a4879d17dfe2aadc1d"
SRC_URI[sha512sum] = "007aa415cccf19a2bcf789c426727dc4032dcb04cc9d11eedc231d2add708c1134d3d5ee5cfbe7de68307c95fff7a30bd306fbd8d53c198a5ef348440440a6ed"

S = "${WORKDIR}/src"

CFLAGS += "-Wno-error=maybe-uninitialized -DALG_CAMELLIA=ALG_NO"

do_compile () {
   make CC='${CC}'
}

do_install () {
   install -d ${D}/${bindir}
   install -m 0755 tpm_server  ${D}/${bindir}
}
