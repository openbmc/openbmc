SUMMARY = "Templatized C++ Command Line Parser"
HOMEPAGE = "http://tclap.sourceforge.net/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0ca8b9c5c5445cfa7af7e78fd27e60ed"

SRCREV = "ec3ddcfe41b0544a4551a57439b6b3682fe31479"
SRC_URI = "git://git.code.sf.net/p/tclap/code;branch=1.2 \
    file://Makefile.am-disable-docs.patch \
"

S = "${WORKDIR}/git"
inherit autotools

ALLOW_EMPTY_${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
