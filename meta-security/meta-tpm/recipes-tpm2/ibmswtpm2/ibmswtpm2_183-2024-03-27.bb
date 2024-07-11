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
LICENSE = "BSD-2-Clause"
SECTION = "securty/tpm"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=1e023f61454ac828b4aa1bc4293f7d5f"
LIC_FILES_CHKSUM += "file://LICENSE;md5=c75e465155c42c14154bf6a2acb7347b"

DEPENDS = "openssl"

SRC_URI = "git://git.code.sf.net/p/ibmswtpm2/tpm2;protocol=https;branch=master \
           file://tune-makefile.patch \
           "
SRCREV = "c37c74438429e1d5fe465232e7bf894b239a2cd4"

UPSTREAM_CHECK_GITTAGREGEX = "rev(?P<pver>\d+(\-\d+)+)"

S = "${WORKDIR}/git/src"

CFLAGS += "-Wno-error=maybe-uninitialized"

do_compile () {
   make CC='${CC}'
}

do_install () {
   install -d ${D}/${bindir}
   install -m 0755 tpm_server  ${D}/${bindir}
}
