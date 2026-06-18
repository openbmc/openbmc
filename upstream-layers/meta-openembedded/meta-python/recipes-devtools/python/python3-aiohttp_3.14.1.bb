SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b5e9e27554f71f0f132919037cb967b2"

SRC_URI[sha256sum] = "307f2cff90a764d329e77040603fa032db89c5c24fdad50c4c15334cba744035"

CVE_PRODUCT = "aiohttp"
CVE_STATUS_GROUPS = "CVE_AIOHTTP_FIX_3_13_4"
CVE_AIOHTTP_FIX_3_13_4[status] = "fixed-version: fixed in 3.13.4"
CVE_AIOHTTP_FIX_3_13_4 = "CVE-2026-22815 CVE-2026-34513 CVE-2026-34514 \
CVE-2026-34515 CVE-2026-34516 CVE-2026-34517 CVE-2026-34518 CVE-2026-34519 \
CVE-2026-34520 CVE-2026-34525"

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
