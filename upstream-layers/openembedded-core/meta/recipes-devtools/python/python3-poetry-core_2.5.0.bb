SUMMARY = "Poetry PEP 517 Build Backend"
DESCRIPTION = "Poetry PEP 517 Build Backend"
HOMEPAGE = "https://github.com/python-poetry/poetry-core"
BUGTRACKER = "https://github.com/python-poetry/poetry-core"
CHANGELOG = "https://github.com/python-poetry/poetry-core/blob/master/CHANGELOG.md"

LICENSE = "Apache-2.0 AND BSD-2-Clause AND BSD-3-Clause AND MIT"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=78c39cfd009863ae44237a7ab1f9cedc \
    file://src/poetry/core/_vendor/fastjsonschema/LICENSE;md5=18950e8362b69c0c617b42b8bd8e7532 \
    file://src/poetry/core/_vendor/lark/LICENSE;md5=ba5edf8d60cf416b5588b1e6f152fcef \
    file://src/poetry/core/_vendor/packaging/LICENSE;md5=faadaedca9251a90b205c9167578ce91 \
    file://src/poetry/core/_vendor/packaging/LICENSE.APACHE;md5=2ee41112a44fe7014dce33e26468ba93 \
    file://src/poetry/core/_vendor/packaging/LICENSE.BSD;md5=7bef9bf4a8e4263634d0597e7ba100b8 \
    file://src/poetry/core/_vendor/tomli/LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5 \
"

SRC_URI[sha256sum] = "81d04c9253b19d0604718268d781867c8f7b2128e5b25bbf1e84141eec6b89c4"

inherit python_poetry_core pypi

RDEPENDS:${PN}:append:class-target = "\
    python3-compression \
    python3-core \
    python3-crypt \
    python3-io \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-pprint \
    python3-shell \
"

RDEPENDS:${PN} += "\
    python3-pip \
"

BBCLASSEXTEND = "native nativesdk"
