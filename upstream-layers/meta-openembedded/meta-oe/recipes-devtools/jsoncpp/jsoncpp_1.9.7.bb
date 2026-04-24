SUMMARY = "JSON C++ lib used to read and write json file."
DESCRIPTION = "Jsoncpp is an implementation of a JSON (http://json.org) reader \
               and writer in C++. JSON (JavaScript Object Notation) is a \
               lightweight data-interchange format. It is easy for humans to \
               read and write. It is easy for machines to parse and generate."

HOMEPAGE = "https://github.com/open-source-parsers/jsoncpp"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d73c165a0f9e86a1342f32d19ec5926"

PE = "1"

SRCREV = "3455302847cf1e4671f1d8f5fa953fd46a7b1404"
SRC_URI = "git://github.com/open-source-parsers/jsoncpp;branch=master;protocol=https;tag=${PV} \
           file://run-ptest \
           "

inherit cmake ptest

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON -DBUILD_OBJECT_LIBS=OFF \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', '-DJSONCPP_WITH_TESTS=ON -DJSONCPP_WITH_POST_BUILD_UNITTEST=OFF', '-DJSONCPP_WITH_TESTS=OFF', d)} \
                  "

DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'rsync-native', '', d)}"
RDEPENDS:${PN}-ptest += "cmake python3-core"

do_install_ptest () {
    cp -r ${B}/bin ${D}${PTEST_PATH}
    cp -r ${S}/test ${D}${PTEST_PATH}
    
    rsync -a ${B}/src ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile \
          --exclude generated
    sed -i -e 's#${B}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${S}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${PYTHON}#/usr/bin/python3#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${WORKDIR}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
}

BBCLASSEXTEND = "native nativesdk"
