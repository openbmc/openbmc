SUMMARY = "Templatized C++ Command Line Parser"
HOMEPAGE = "http://tclap.sourceforge.net/" 
LICENSE = "MIT" 
LIC_FILES_CHKSUM = "file://COPYING;md5=c8ab0ff134bcc584d0e6b5b9f8732453"

SRCREV = "3627d9402e529770df9b0edf2aa8c0e0d6c6bb41"
SRC_URI = "git://git.code.sf.net/p/tclap/code \
    file://Makefile.am-disable-docs.patch \
" 

S = "${WORKDIR}/git"
inherit autotools

ALLOW_EMPTY_${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
