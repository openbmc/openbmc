SUMMARY = "open-source formatting library for C++"
DESCRIPTION = "{fmt} is an open-source formatting library for C++. It can be used as a safe and fast alternative to (s)printf and iostreams."
HOMEPAGE = "https://fmt.dev"
LICENSE = "MIT WITH fmt-exception"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec080902ed8f82f5a97ed13e8042634 \
                    file://doc/LICENSE-exception;md5=b9257785fc4f3803a4b71b76c1412729"

SRC_URI = "git://github.com/fmtlib/fmt;branch=main;protocol=https;tag=${PV} \
           file://0001-Workaround-an-ABI-issue-in-spdlog.patch \
           file://run-ptest \
           file://0001-Fix-fallback-uint128-bitwise-not-4813.patch \
           "
SRCREV = "1be298e1bd68957e4cd352e1f676f00e07dcfb57"

inherit cmake ptest

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DFMT_TEST=ON', '', d)}"

do_install_ptest(){
	for t in ${B}/bin/*-test; do
		install $t ${D}${PTEST_PATH}/
	done
}

BBCLASSEXTEND = "native nativesdk"
