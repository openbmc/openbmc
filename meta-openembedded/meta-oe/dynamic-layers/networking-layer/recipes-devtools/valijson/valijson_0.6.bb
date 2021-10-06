SUMMARY = "Header-only C++ library for JSON Schema validation"
HOMEPAGE = "https://github.com/tristanpenman/valijson"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=015106c62262b2383f6c72063f0998f2"

SRC_URI = "git://github.com/tristanpenman/valijson.git"
SRCREV = "2dfc7499a31b84edef71189f4247919268ebc74e"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DINSTALL_HEADERS=1 -DBUILD_TESTS=0"

DEPENDS = "curlpp"

# valijson is a header only C++ library, so the main package will be empty.
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

do_install() {
    install -d ${D}${includedir}/compat
    install -d ${D}${includedir}/valijson
    install -d ${D}${includedir}/valijson/adapters
    install -d ${D}${includedir}/valijson/constraints
    install -d ${D}${includedir}/valijson/internal
    install -d ${D}${includedir}/valijson/utils

    install -m 0644 ${S}/include/compat/* ${D}${includedir}/compat
    install -D -m 0644 ${S}/include/valijson/*.hpp -t ${D}${includedir}/valijson
    install -D -m 0644 ${S}/include/valijson/adapters/*.hpp -t ${D}${includedir}/valijson/adapters
    install -D -m 0644 ${S}/include/valijson/constraints/*.hpp -t ${D}${includedir}/valijson/constraints
    install -D -m 0644 ${S}/include/valijson/internal/*.hpp -t ${D}${includedir}/valijson/internal
    install -D -m 0644 ${S}/include/valijson/utils/*.hpp -t ${D}${includedir}/valijson/utils
}
