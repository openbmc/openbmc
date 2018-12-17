SUMMARY = "Modern, powerful open source cross-platform C++ class libraries"
DESCRIPTION = "Modern, powerful open source C++ class libraries and frameworks for building network- and internet-based applications that run on desktop, server, mobile and embedded systems."
HOMEPAGE = "http://pocoproject.org/"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4267f48fc738f50380cbeeb76f95cebc"

# These dependencies are required by Foundation
DEPENDS = "libpcre zlib"

SRC_URI = " \
    git://github.com/pocoproject/poco.git;branch=poco-${PV} \
    file://0001-Don-t-try-to-install-non-existing-Encodings-testsuit.patch \
    file://run-ptest \
   "
SRCREV = "daf00da73ab917e4d4581e3aa00c0ee61e0cc58b"

S = "${WORKDIR}/git"

inherit cmake ptest

# By default the most commonly used poco components are built
# Foundation is built anyway and doesn't need to be listed explicitly
# these don't have dependencies outside oe-core
PACKAGECONFIG ??= "XML JSON MongoDB PDF Util Net NetSSL Crypto Data DataSQLite Zip Encodings Redis"

PACKAGECONFIG[XML] = "-DENABLE_XML=ON -DEXPAT_LIBRARY:STRING=expat,-DENABLE_XML=OFF,expat"
PACKAGECONFIG[JSON] = "-DENABLE_JSON=ON,-DENABLE_JSON=OFF"
PACKAGECONFIG[MongoDB] = "-DENABLE_MONGODB=ON,-DENABLE_MONGODB=OFF"
PACKAGECONFIG[PDF] = "-DENABLE_PDF=ON,-DENABLE_PDF=OFF,zlib"
PACKAGECONFIG[Util] = "-DENABLE_UTIL=ON,-DENABLE_UTIL=OFF"
PACKAGECONFIG[Net] = "-DENABLE_NET=ON,-DENABLE_NET=OFF"
PACKAGECONFIG[NetSSL] = "-DENABLE_NETSSL=ON -DOPENSSL_SSL_LIBRARY:STRING=ssl -DOPENSSL_CRYPTO_LIBRARY:STRING=crypto,-DENABLE_NETSSL=OFF,openssl"
PACKAGECONFIG[Crypto] = "-DENABLE_CRYPTO=ON -DOPENSSL_SSL_LIBRARY:STRING=ssl -DOPENSSL_CRYPTO_LIBRARY:STRING=crypto,-DENABLE_CRYPTO=OFF,openssl"
PACKAGECONFIG[Data] = "-DENABLE_DATA=ON,-DENABLE_DATA=OFF"
PACKAGECONFIG[DataSQLite] = "-DENABLE_DATA_SQLITE=ON -DSQLITE3_LIBRARY:STRING=sqlite3,-DENABLE_DATA_SQLITE=OFF,sqlite3"
PACKAGECONFIG[Zip] = "-DENABLE_ZIP=ON,-DENABLE_ZIP=OFF"
PACKAGECONFIG[Encodings] = "-DENABLE_ENCODINGS=ON,-DENABLE_ENCODINGS=OFF"
PACKAGECONFIG[Redis] = "-DENABLE_REDIS=ON,-DENABLE_REDIS=OFF"

# Additional components not build by default,
# they might have dependencies not included in oe-core
# or they don't work on all architectures
PACKAGECONFIG[mod_poco] = "-DENABLE_APACHECONNECTOR=ON,-DENABLE_APACHECONNECTOR=OFF,apr apache2"
PACKAGECONFIG[CppParser] = "-DENABLE_CPPPARSER=ON,-DENABLE_CPPPARSER=OFF"
PACKAGECONFIG[DataMySQL] = "-DENABLE_DATA_MYSQL=ON -DMYSQL_LIB:STRING=mysqlclient_r,-DENABLE_DATA_MYSQL=OFF,mariadb"
PACKAGECONFIG[DataODBC] = "-DENABLE_DATA_ODBC=ON,-DENABLE_DATA_ODBC=OFF,libiodbc"
PACKAGECONFIG[PageCompiler] = "-DENABLE_PAGECOMPILER=ON,-DENABLE_PAGECOMPILER=OFF"
PACKAGECONFIG[PageCompilerFile2Page] = "-DENABLE_PAGECOMPILER_FILE2PAGE=ON,-DENABLE_PAGECOMPILER_FILE2PAGE=OFF"
PACKAGECONFIG[SevenZip] = "-DENABLE_SEVENZIP=ON,-DENABLE_SEVENZIP=OFF"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=RelWithDebInfo -DPOCO_UNBUNDLED=ON \
                  -DZLIB_LIBRARY_RELEASE:STRING=z -DPCRE_LIBRARY:STRING=pcre \
                 ${@bb.utils.contains('PTEST_ENABLED', '1', '-DENABLE_TESTS=ON ', '', d)}"

# For the native build we want to use the bundled version
EXTRA_OECMAKE_append_class-native = " -DPOCO_UNBUNDLED=OFF"

# do not use rpath
EXTRA_OECMAKE_append = " -DCMAKE_SKIP_RPATH=ON"

python populate_packages_prepend () {
    poco_libdir = d.expand('${libdir}')
    pn = d.getVar("PN")
    packages = []
    testrunners = []

    def hook(f, pkg, file_regex, output_pattern, modulename):
        packages.append(pkg)
        testrunners.append(modulename)

    do_split_packages(d, poco_libdir, '^libPoco(.*)\.so\..*$',
                    'poco-%s', 'Poco %s component', extra_depends='', prepend=True, hook=hook)

    d.setVar("RRECOMMENDS_%s" % pn, " ".join(packages))
    d.setVar("POCO_TESTRUNNERS", "\n".join(testrunners))
}

do_install_ptest () {
       cp -rf ${B}/bin/ ${D}${PTEST_PATH}
       cp -f ${B}/lib/libCppUnit.so* ${D}${libdir}
       cp -rf ${B}/*/testsuite/data ${D}${PTEST_PATH}/bin/
       find "${D}${PTEST_PATH}" -executable -exec chrpath -d {} \;
       echo "${POCO_TESTRUNNERS}" > "${D}${PTEST_PATH}/testrunners"
}

PACKAGES_DYNAMIC = "poco-.*"

# "poco" is a metapackage which pulls in all Poco components
ALLOW_EMPTY_${PN} = "1"

# cppunit is only built if tests are enabled
PACKAGES =+ "${PN}-cppunit"
FILES_${PN}-cppunit += "${libdir}/libCppUnit.so*"
ALLOW_EMPTY_${PN}-cppunit = "1"

RDEPENDS_${PN}-ptest += "${PN}-cppunit"

BBCLASSEXTEND = "native"
