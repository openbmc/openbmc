SUMMARY = "Modern, powerful open source cross-platform C++ class libraries"
DESCRIPTION = "Modern, powerful open source C++ class libraries and frameworks for building network- and internet-based applications that run on desktop, server, mobile and embedded systems."
SECTION = "libs"
HOMEPAGE = "http://pocoproject.org/"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4267f48fc738f50380cbeeb76f95cebc"

# These dependencies are required by Foundation
DEPENDS = "libpcre zlib"

inherit cmake ptest

BBCLASSEXTEND = "native"

SRC_URI = " \
    https://github.com/pocoproject/poco/archive/poco-${PV}-release.tar.gz \
    file://run-ptest \
   "

SRC_URI[md5sum] = "a4b755d47303b20a0e2586f281d05a36"
SRC_URI[sha256sum] = "6dbbc2018912ad9af6af96f605933ed91354a1e7423e5dbd04d8e9a2b2d15c05"

S = "${WORKDIR}/poco-poco-${PV}-release"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=RelWithDebInfo -DPOCO_UNBUNDLED=ON \
                 ${@bb.utils.contains('PTEST_ENABLED', '1', '-DENABLE_TESTS=ON ', '', d)}"

# For the native build we want to use the bundled version
EXTRA_OECMAKE_append_class-native = " -DPOCO_UNBUNDLED=OFF"

# do not use rpath
EXTRA_OECMAKE_append = " -DCMAKE_SKIP_RPATH=ON"

# By default the most commonly used poco components are built
# Foundation is built anyway and doesn't need to be listed explicitly
# these don't have dependencies outside oe-core
PACKAGECONFIG ??= "XML JSON MongoDB PDF Util Net NetSSL Crypto Data DataSQLite Zip"

PACKAGECONFIG[XML] = "-DENABLE_XML=ON,-DENABLE_XML=OFF,expat"
PACKAGECONFIG[JSON] = "-DENABLE_JSON=ON,-DENABLE_JSON=OFF"
PACKAGECONFIG[MongoDB] = "-DENABLE_MONGODB=ON,-DENABLE_MONGODB=OFF"
PACKAGECONFIG[PDF] = "-DENABLE_PDF=ON,-DENABLE_PDF=OFF,zlib"
PACKAGECONFIG[Util] = "-DENABLE_UTIL=ON,-DENABLE_UTIL=OFF"
PACKAGECONFIG[Net] = "-DENABLE_NET=ON,-DENABLE_NET=OFF"
PACKAGECONFIG[NetSSL] = "-DENABLE_NETSSL=ON,-DENABLE_NETSSL=OFF,openssl"
PACKAGECONFIG[Crypto] = "-DENABLE_CRYPTO=ON,-DENABLE_CRYPTO=OFF,openssl"
PACKAGECONFIG[Data] = "-DENABLE_DATA=ON,-DENABLE_DATA=OFF"
PACKAGECONFIG[DataSQLite] = "-DENABLE_DATA_SQLITE=ON,-DENABLE_DATA_SQLITE=OFF,sqlite3"
PACKAGECONFIG[Zip] = "-DENABLE_ZIP=ON,-DENABLE_ZIP=OFF"

# Additional components not build by default,
# they might have dependencies not included in oe-core
# or they don't work on all architectures
PACKAGECONFIG[mod_poco] = "-DENABLE_APACHECONNECTOR=ON,-DENABLE_APACHECONNECTOR=OFF,apr apache2"
PACKAGECONFIG[CppParser] = "-DENABLE_CPPPARSER=ON,-DENABLE_CPPPARSER=OFF"
PACKAGECONFIG[DataMySQL] = "-DENABLE_DATA_MYSQL=ON,-DENABLE_DATA_MYSQL=OFF,mariadb"
PACKAGECONFIG[DataODBC] = "-DENABLE_DATA_ODBC=ON,-DENABLE_DATA_ODBC=OFF,libiodbc"
PACKAGECONFIG[PageCompiler] = "-DENABLE_PAGECOMPILER=ON,-DENABLE_PAGECOMPILER=OFF"
PACKAGECONFIG[PageCompilerFile2Page] = "-DENABLE_PAGECOMPILER_FILE2PAGE=ON,-DENABLE_PAGECOMPILER_FILE2PAGE=OFF"
PACKAGECONFIG[SevenZip] = "-DENABLE_SEVENZIP=ON,-DENABLE_SEVENZIP=OFF"

# Make a package for each library
PACKAGES = "${PN}-dbg ${POCO_PACKAGES}"
python __anonymous () {
    packages = []
    testrunners = []
    components = d.getVar("PACKAGECONFIG", True).split()
    components.append("Foundation")
    for lib in components:
        pkg = ("poco-%s" % lib.lower()).replace("_","")
        packages.append(pkg)
        if not d.getVar("FILES_%s" % pkg, True):
            d.setVar("FILES_%s" % pkg, "${libdir}/libPoco%s.so.*" % lib)
        testrunners.append("%s" % lib)

    d.setVar("POCO_PACKAGES", " ".join(packages))
    d.setVar("POCO_TESTRUNNERS", "\n".join(testrunners))
}

# "poco" is a metapackage which pulls in all Poco components
PACKAGES += "${PN}"
RRECOMMENDS_${PN} += "${POCO_PACKAGES}"
RRECOMMENDS_${PN}_class-native = ""
ALLOW_EMPTY_${PN} = "1"

# -dev last to pick up the remaining stuff
PACKAGES += "${PN}-dev ${PN}-staticdev"
FILES_${PN}-dev = "${includedir} ${libdir}/libPoco*.so ${libdir}/cmake"
FILES_${PN}-staticdev = "${libdir}/libPoco*.a"

# ptest support
FILES_${PN}-dbg += "${PTEST_PATH}/bin/.debug"

# cppunit is only built if tests are enabled
PACKAGES += "${PN}-cppunit"
FILES_${PN}-cppunit += "${libdir}/libCppUnit.so*"
ALLOW_EMPTY_${PN}-cppunit = "1"

RDEPENDS_${PN}-ptest += "${PN}-cppunit"

do_install_ptest () {
       cp -rf ${B}/bin/ ${D}${PTEST_PATH}
       cp -f ${B}/lib/libCppUnit.so* ${D}${libdir}
       cp -rf ${B}/*/testsuite/data ${D}${PTEST_PATH}/bin/
       find "${D}${PTEST_PATH}" -executable -exec chrpath -d {} \;
       echo "${POCO_TESTRUNNERS}" > "${D}${PTEST_PATH}/testrunners"
}
