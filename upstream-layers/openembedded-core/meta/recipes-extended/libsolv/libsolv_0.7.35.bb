SUMMARY = "Library for solving packages and reading repositories"
DESCRIPTION = "This is libsolv, a free package dependency solver using a satisfiability algorithm for solving packages and reading repositories"
HOMEPAGE = "https://github.com/openSUSE/libsolv"
BUGTRACKER = "https://github.com/openSUSE/libsolv/issues"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

DEPENDS = "expat zlib zstd"

SRC_URI = "git://github.com/openSUSE/libsolv.git;branch=master;protocol=https;tag=${PV} \
           file://0001-utils-Conside-musl-when-wrapping-qsort_r.patch \
           file://run-ptest \
"

SRCREV = "fb4b4340d46108cb365113e432642d6024886c7a"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

inherit cmake ptest

PACKAGECONFIG ??= "${@bb.utils.contains('PACKAGE_CLASSES','package_rpm','rpm','',d)}"
PACKAGECONFIG[rpm] = "-DENABLE_RPMMD=ON -DENABLE_RPMDB=ON,,rpm"

EXTRA_OECMAKE = "-DMULTI_SEMANTICS=ON -DENABLE_COMPLEX_DEPS=ON -DENABLE_ZSTD_COMPRESSION=ON -DENABLE_STATIC=ON"

PACKAGES =+ "${PN}-tools ${PN}ext"

FILES:${PN}-tools = "${bindir}/*"
FILES:${PN}ext = "${libdir}/${PN}ext.so.*"

BBCLASSEXTEND = "native nativesdk"

do_compile_ptest() {
    cmake_runcmake_build --target testsolv
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tools
    install -d ${D}${PTEST_PATH}/test
    install -m 0755 ${B}/tools/testsolv ${D}${PTEST_PATH}/tools/
    install -m 0755 ${S}/test/runtestcases.sh ${D}${PTEST_PATH}/test/
    cp -r ${S}/test/testcases ${D}${PTEST_PATH}/test/
}

RDEPENDS:${PN}-ptest += "bash"
