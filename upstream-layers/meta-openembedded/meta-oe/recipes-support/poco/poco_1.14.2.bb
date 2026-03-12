SUMMARY = "Modern, powerful open source cross-platform C++ class libraries"
DESCRIPTION = "Modern, powerful open source C++ class libraries and frameworks for building network- and internet-based applications that run on desktop, server, mobile and embedded systems."
HOMEPAGE = "http://pocoproject.org/"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4267f48fc738f50380cbeeb76f95cebc"

# These dependencies are required by Foundation
DEPENDS = "libpcre2 utf8proc zlib"

SRC_URI = "git://github.com/pocoproject/poco.git;branch=poco-${PV};protocol=https;tag=poco-${PV}-release \
           file://0001-cppignore.lnx-Ignore-PKCS12-and-testLaunch-test.patch \
           file://0002-DataTest-disable-testSQLChannel-test.patch \
           file://run-ptest \
           "
SRCREV = "96d182a99303fb068575294b36f0cc20da2e7b25"

UPSTREAM_CHECK_GITTAGREGEX = "poco-(?P<pver>\d+(\.\d+)+)"


inherit cmake ptest

# By default the most commonly used poco components are built
# Foundation is built anyway and doesn't need to be listed explicitly
# these don't have dependencies outside oe-core
PACKAGECONFIG ??= "XML JSON PDF Util Net NetSSL Crypto JWT Data DataPostgreSQL DataSQLite DNSSDAvahi Zip Encodings Prometheus"
# MongoDB does not build for all architectures yet keep in sync with COMPATIBLE_HOST list in mongodb recipe
# and mongodb needs meta-python enabled as well
PACKAGECONFIG:remove:riscv32 = "MongoDB"
PACKAGECONFIG:remove:riscv64 = "MongoDB"
PACKAGECONFIG:remove:mipsarch = "MongoDB"
PACKAGECONFIG:remove:powerpc = "MongoDB"
# Following options have dependencies on recipes which don't have native variant
PACKAGECONFIG:remove:class-native = "MongoDB DataODBC DataPostgreSQL DNSSDAvahi"

PACKAGECONFIG[XML] = "-DENABLE_XML=ON,-DENABLE_XML=OFF,expat"
PACKAGECONFIG[JSON] = "-DENABLE_JSON=ON,-DENABLE_JSON=OFF"
PACKAGECONFIG[PDF] = "-DENABLE_PDF=ON,-DENABLE_PDF=OFF,libpng zlib"
PACKAGECONFIG[Util] = "-DENABLE_UTIL=ON,-DENABLE_UTIL=OFF"
PACKAGECONFIG[Net] = "-DENABLE_NET=ON,-DENABLE_NET=OFF"
PACKAGECONFIG[NetSSL] = "-DENABLE_NETSSL=ON,-DENABLE_NETSSL=OFF,openssl"
PACKAGECONFIG[Crypto] = "-DENABLE_CRYPTO=ON,-DENABLE_CRYPTO=OFF,openssl"
PACKAGECONFIG[JWT] = "-DENABLE_JWT=ON,-DENABLE_JWT=OFF,openssl"
PACKAGECONFIG[Data] = "-DENABLE_DATA=ON,-DENABLE_DATA=OFF"
PACKAGECONFIG[DataPostgreSQL] = "-DENABLE_DATA_POSTGRESQL=ON,-DENABLE_DATA_POSTGRESQL=OFF,postgresql,postgresql"
PACKAGECONFIG[DataSQLite] = "-DENABLE_DATA_SQLITE=ON,-DENABLE_DATA_SQLITE=OFF,sqlite3"
PACKAGECONFIG[DNSSDAvahi] = "-DENABLE_DNSSD=ON -DENABLE_DNSSD_AVAHI=ON,-DENABLE_DNSSD=OFF -DENABLE_DNSSD_AVAHI=OFF,avahi"
PACKAGECONFIG[Zip] = "-DENABLE_ZIP=ON,-DENABLE_ZIP=OFF"
PACKAGECONFIG[Encodings] = "-DENABLE_ENCODINGS=ON,-DENABLE_ENCODINGS=OFF"
PACKAGECONFIG[Prometheus] = "-DENABLE_PROMETHEUS=ON,-DENABLE_PROMETHEUS=OFF"

# Additional components not build by default,
# they might have dependencies not included in oe-core
# or they don't work on all architectures
PACKAGECONFIG[mod_poco] = "-DENABLE_APACHECONNECTOR=ON,-DENABLE_APACHECONNECTOR=OFF,apr apache2"
PACKAGECONFIG[CppParser] = "-DENABLE_CPPPARSER=ON,-DENABLE_CPPPARSER=OFF"
PACKAGECONFIG[DataMySQL] = "-DENABLE_DATA_MYSQL=ON -DMYSQL_LIB:STRING=mysqlclient_r,-DENABLE_DATA_MYSQL=OFF,mariadb"
PACKAGECONFIG[DataODBC] = "-DENABLE_DATA_ODBC=ON,-DENABLE_DATA_ODBC=OFF,libiodbc"
PACKAGECONFIG[ActiveRecord] = "-DENABLE_ACTIVERECORD=ON,-DENABLE_ACTIVERECORD=OFF"
PACKAGECONFIG[ActiveRecordCompiler] = "-DENABLE_ACTIVERECORD_COMPILER=ON,-DENABLE_ACTIVERECORD_COMPILER=OFF"
PACKAGECONFIG[MongoDB] = "-DENABLE_MONGODB=ON,-DENABLE_MONGODB=OFF"
PACKAGECONFIG[PageCompiler] = "-DENABLE_PAGECOMPILER=ON,-DENABLE_PAGECOMPILER=OFF"
PACKAGECONFIG[PageCompilerFile2Page] = "-DENABLE_PAGECOMPILER_FILE2PAGE=ON,-DENABLE_PAGECOMPILER_FILE2PAGE=OFF"
PACKAGECONFIG[Redis] = "-DENABLE_REDIS=ON,-DENABLE_REDIS=OFF"
PACKAGECONFIG[SevenZip] = "-DENABLE_SEVENZIP=ON,-DENABLE_SEVENZIP=OFF"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=RelWithDebInfo -DPOCO_UNBUNDLED=ON \
                  -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
                 ${@bb.utils.contains('PTEST_ENABLED', '1', '-DENABLE_TESTS=ON ', '', d)}"

# For the native build we want to use the bundled version
EXTRA_OECMAKE:append:class-native = " -DPOCO_UNBUNDLED=OFF"

# do not use rpath
EXTRA_OECMAKE:append = " -DCMAKE_SKIP_RPATH=ON"

LDFLAGS:append:riscv32 = "${@bb.utils.contains('PACKAGECONFIG', 'Prometheus', ' -Wl,--no-as-needed -latomic -Wl,--as-needed', '', d)}"
LDFLAGS:append:mips = "${@bb.utils.contains('PACKAGECONFIG', 'Prometheus', ' -Wl,--no-as-needed -latomic -Wl,--as-needed', '', d)}"
LDFLAGS:append:powerpc = "${@bb.utils.contains('PACKAGECONFIG', 'Prometheus', ' -Wl,--no-as-needed -latomic -Wl,--as-needed', '', d)}"
LDFLAGS:append:x86 = "${@bb.utils.contains('PACKAGECONFIG', 'Prometheus', ' -Wl,--no-as-needed -latomic -Wl,--as-needed', '', d)}"

python populate_packages:prepend () {
    poco_libdir = d.expand('${libdir}')
    pn = d.getVar("PN")
    packages = []

    def hook(f, pkg, file_regex, output_pattern, modulename):
        packages.append(pkg)

    do_split_packages(d, poco_libdir, r'^libPoco(.*)\.so\..*$',
                    'poco-%s', 'Poco %s component', extra_depends='', prepend=True, hook=hook)

    d.setVar("RRECOMMENDS:%s" % pn, " ".join(packages))
}

do_install:append() {
    # fix buildpaths
    if [ -e ${D}${nonarch_libdir}/cmake/Poco/PocoPDFTargets.cmake ]; then
        sed -i 's#${RECIPE_SYSROOT}##' ${D}${nonarch_libdir}/cmake/Poco/PocoPDFTargets.cmake
    fi
    if [ -e ${D}${nonarch_libdir}/cmake/Poco/PocoDNSSDAvahiTargets.cmake ]; then
        sed -i 's#${RECIPE_SYSROOT}##g' ${D}${nonarch_libdir}/cmake/Poco/PocoDNSSDAvahiTargets.cmake
    fi
}

do_install_ptest () {
       cp -rf ${B}/bin/ ${D}${PTEST_PATH}
       cp -f ${B}/lib/libCppUnit.so* ${D}${libdir}
       if ${@bb.utils.contains('PACKAGECONFIG', 'DataPostgreSQL', 'true', 'false', d)}; then
            cp -f ${B}/lib/libPocoDataTest.so* ${D}${libdir}
       fi
       cp -rf ${S}/*/testsuite/data ${D}${PTEST_PATH}/bin/
       find "${D}${PTEST_PATH}" -executable -exec chrpath -d {} \;
       rm -f ${D}${PTEST_PATH}/testrunners
       for f in ${D}${PTEST_PATH}/bin/*-testrunner; do
            echo `basename $f` >> ${D}${PTEST_PATH}/testrunners
       done
       install -Dm 0644 ${S}/cppignore.lnx ${D}${PTEST_PATH}/cppignore.lnx
       install ${B}/bin/TestLibrary.so ${D}${libdir}
       install -D ${B}/bin/TestApp ${D}${bindir}/TestApp
}

PACKAGES_DYNAMIC = "poco-.*"

# "poco" is a metapackage which pulls in all Poco components
ALLOW_EMPTY:${PN} = "1"

# cppunit and datatest is only built if tests are enabled
PACKAGES =+ "${PN}-cppunit ${PN}-datatest"
FILES:${PN}-cppunit += "${libdir}/libCppUnit.so*"
ALLOW_EMPTY:${PN}-cppunit = "1"
FILES:${PN}-datatest += "${libdir}/libPocoDataTest.so*"
ALLOW_EMPTY:${PN}-datatest = "1"
FILES:${PN}-ptest += "${bindir}/TestApp ${libdir}/TestLibrary.so"

RDEPENDS:${PN}-ptest += "${PN}-cppunit ${PN}-datatest"
RDEPENDS:${PN}-ptest += "${@bb.utils.contains('PACKAGECONFIG', 'MongoDB', 'mongodb', '', d)}"
RDEPENDS:${PN}-ptest += "${@bb.utils.contains('PACKAGECONFIG', 'Redis', 'redis', '', d)}"

BBCLASSEXTEND = "native"
