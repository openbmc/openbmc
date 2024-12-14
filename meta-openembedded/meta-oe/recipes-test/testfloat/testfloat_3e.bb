SUMMARY = "Berkeley TestFloat 3e"
DESCRIPTION = "Berkeley TestFloat is a small collection of programs for \
    testing that an implementation of binary floating-point conforms to the \
    IEEE Standard for Floating-Point Arithmetic."

HOMEPAGE = "http://www.jhauser.us/arithmetic/TestFloat.html"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://TestFloat-${PV}/COPYING.txt;md5=d467c2d231054347e8fd885ac06e7b2b"

SRC_URI = "\
    http://www.jhauser.us/arithmetic/TestFloat-3e.zip;name=TestFloat \
    http://www.jhauser.us/arithmetic/SoftFloat-3e.zip;name=SoftFloat \
    file://0001-Makefile-for-cross-compile-SoftFloat.patch \
    file://0002-Makefile-for-cross-compile-TestFloat.patch \
"
SRC_URI[TestFloat.md5sum] = "e70a1e6c6732abf79645a6dcca69a654"
SRC_URI[TestFloat.sha256sum] = "6d4bdf0096b48a653aa59fc203a9e5fe18b5a58d7a1b715107c7146776a0aad6"
SRC_URI[SoftFloat.md5sum] = "7dac954ea4aed0697cbfee800ba4f492"
SRC_URI[SoftFloat.sha256sum] = "21130ce885d35c1fe73fc1e1bf2244178167e05c6747cad5f450cc991714c746"

UPSTREAM_CHECK_URI = "http://www.jhauser.us/arithmetic/TestFloat.html"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

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
