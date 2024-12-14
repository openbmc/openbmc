SUMMARY = "The tpm-tools package contains commands to allow the platform administrator the ability to manage and diagnose the platform's TPM."
DESCRIPTION = " \
  The tpm-tools package contains commands to allow the platform administrator \
  the ability to manage and diagnose the platform's TPM.  Additionally, the \
  package contains commands to utilize some of the capabilities available \
  in the TPM PKCS#11 interface implemented in the openCryptoki project. \
  "
SECTION = "tpm"
LICENSE = "CPL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=059e8cd6165cb4c31e351f2b69388fd9"

DEPENDS = "libtspi openssl perl-native"
DEPENDS:class-native = "trousers-native"

SRCREV = "bf43837575c5f7d31865562dce7778eae970052e"
SRC_URI = " \
    git://git.code.sf.net/p/trousers/tpm-tools;branch=master \
    file://tpm-tools-extendpcr.patch \
    file://04-fix-FTBFS-clang.patch \
    file://openssl1.1_fix.patch \
    "

inherit autotools-brokensep gettext

S = "${UNPACKDIR}/git"

# Compile failing with gcc-14
CFLAGS += " -Wno-incompatible-pointer-types -Wno-stringop-truncation -Wno-error=implicit-function-declaration"
BUILD_CFLAGS += " -Wno-incompatible-pointer-types -Wno-stringop-truncation -Wno-error=implicit-function-declaration"

do_configure:prepend () {
	mkdir -p po
	mkdir -p m4
	cp -R po_/* po/
	touch po/Makefile.in.in
	touch m4/Makefile.am
}

BBCLASSEXTEND = "native"
