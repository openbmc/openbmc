SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=748073912af33aa59430d3702aa32d41"

SRC_URI[sha256sum] = "a949eee43d3782f2daae4f4a2819b2cb9b0c5d3b7f7a927067cc84dafdbb9f88"

CVE_PRODUCT = "aiohttp"

inherit python_setuptools_build_meta pypi 

DEPENDS = "python3-pkgconfig-native"

PACKAGECONFIG ??= ""
PACKAGECONFIG[extras] = ",,,python3-aiodns python3-brotli"

RDEPENDS:${PN} = "\
    python3-aiohappyeyeballs \
    python3-aiosignal \
    python3-async-timeout \
    python3-attrs \
    python3-frozenlist \
    python3-misc \
    python3-multidict \
    python3-propcache \
    python3-yarl \
"

CFLAGS:append:toolchain-gcc:arm = " -flax-vector-conversions"

BBCLASSEXTEND = "native nativesdk"
