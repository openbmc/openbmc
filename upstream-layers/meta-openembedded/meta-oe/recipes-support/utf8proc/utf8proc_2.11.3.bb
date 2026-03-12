SUMMARY = "library that provides operations for data in the UTF-8 encoding"
DESCRIPTION = "utf8proc is a small, clean C library that provides Unicode \
normalization, case-folding, and other operations for data in the UTF-8 \
encoding, supporting Unicode version 16.0"
HOMEPAGE = "https://juliastrings.github.io/utf8proc/"
SECTION = "libs"

LICENSE = "MIT & Unicode-3.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=96d5a3ba306e0f24fb289427af484408"

SRC_URI = "\
    git://github.com/JuliaStrings/utf8proc;protocol=https;branch=master;tag=v${PV} \
    file://run-ptest \
"
SRCREV = "e5e799221b45bbb90f5fdc5c69b6b8dfbf017e78"

inherit cmake ptest

EXTRA_OECMAKE = "\
    -DBUILD_SHARED_LIBS=ON \
    ${@bb.utils.contains('PTEST_ENABLED', '1', '-DBUILD_TESTING=ON -DUTF8PROC_ENABLE_TESTING=ON ', '', d)} \
"

do_install_ptest() {
    # this list and run-ptest needs to be updated on upgrade (the project uses add_test feature)
    for t in "case" custom iterate misc printproperty valid charwidth graphemetest normtest; do
        install -m 0755 ${B}/$t ${D}${PTEST_PATH}/
    done
    install -d ${D}${PTEST_PATH}/data
    install -m 0644 ${B}/data/GraphemeBreakTest.txt ${D}${PTEST_PATH}/data/
    install -m 0644 ${B}/data/NormalizationTest.txt ${D}${PTEST_PATH}/data/
}

BBCLASSEXTEND = "native"
