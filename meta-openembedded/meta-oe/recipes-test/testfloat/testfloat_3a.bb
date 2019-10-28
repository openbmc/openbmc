DESCRIPTION = "Berkeley TestFloat is a small collection of programs for \
    testing that an implementation of binary floating-point conforms to the \
    IEEE Standard for Floating-Point Arithmetic."

HOMEPAGE = "http://www.jhauser.us/arithmetic/TestFloat.html"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://TestFloat-${PV}/COPYING.txt;md5=e45c175a323b5727777fb6bd4b26eafc"

SRC_URI = "\
    http://www.jhauser.us/arithmetic/TestFloat-3a.zip;name=TestFloat \
    http://www.jhauser.us/arithmetic/SoftFloat-3a.zip;name=SoftFloat \
    file://0001-Makefile-for-cross-compile-SoftFloat.patch \
    file://0002-Makefile-for-cross-compile-TestFloat.patch \
"
SRC_URI[TestFloat.md5sum] = "5a124e85ab74c5e52da27d401cea6cc3"
SRC_URI[TestFloat.sha256sum] = "fa258b5b3c751656a372051adee4183e19ad4763032322eb7a87dfb9e2c22c75"
SRC_URI[SoftFloat.md5sum] = "e53bd4550cf99690642c41374d188517"
SRC_URI[SoftFloat.sha256sum] = "946fd23180559d60eb6683dda1cf8b142f5426dedfefb97b03c6afdfd70ee9e0"

S = "${WORKDIR}"

do_compile(){
    oe_runmake -C SoftFloat-${PV}/build/Linux-Cross-Compile/
    oe_runmake -C TestFloat-${PV}/build/Linux-Cross-Compile/
}

do_install(){
    install -d ${D}/${bindir}
    install ${S}/TestFloat-${PV}/build/Linux-Cross-Compile/testfloat     ${D}/${bindir}
    install ${S}/TestFloat-${PV}/build/Linux-Cross-Compile/testfloat_gen ${D}/${bindir}
    install ${S}/TestFloat-${PV}/build/Linux-Cross-Compile/testfloat_ver ${D}/${bindir}
    install ${S}/TestFloat-${PV}/build/Linux-Cross-Compile/testsoftfloat ${D}/${bindir}
    install ${S}/TestFloat-${PV}/build/Linux-Cross-Compile/timesoftfloat ${D}/${bindir}
}
