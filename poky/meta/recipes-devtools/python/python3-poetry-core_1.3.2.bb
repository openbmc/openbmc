SUMMARY = "Poetry PEP 517 Build Backend"
DESCRIPTION = "Poetry PEP 517 Build Backend"
HOMEPAGE = "https://github.com/python-poetry/poetry-core"
BUGTRACKER = "https://github.com/python-poetry/poetry-core"
CHANGELOG = "https://github.com/python-poetry/poetry-core/blob/master/CHANGELOG.md"

LICENSE = "Apache-2.0 & BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=78c39cfd009863ae44237a7ab1f9cedc \
    file://src/poetry/core/_vendor/attr/_version_info.py;beginline=1;endline=1;md5=b2dccaa94b3629a08bfb4f983cad6f89 \
    file://src/poetry/core/_vendor/attrs/LICENSE;md5=5e55731824cf9205cfabeab9a0600887 \
    file://src/poetry/core/_vendor/jsonschema/COPYING;md5=7a60a81c146ec25599a3e1dabb8610a8 \
    file://src/poetry/core/_vendor/lark/LICENSE;md5=b37b83a9cf129d92ee65aaa71c01ce72 \
    file://src/poetry/core/_vendor/packaging/LICENSE;md5=faadaedca9251a90b205c9167578ce91 \
    file://src/poetry/core/_vendor/packaging/LICENSE.APACHE;md5=2ee41112a44fe7014dce33e26468ba93 \
    file://src/poetry/core/_vendor/packaging/LICENSE.BSD;md5=7bef9bf4a8e4263634d0597e7ba100b8 \
    file://src/poetry/core/_vendor/pyparsing/LICENSE;md5=657a566233888513e1f07ba13e2f47f1 \
    file://src/poetry/core/_vendor/pyrsistent/LICENSE.mit;md5=b695eb9c6e7a6fb1b1bc2d193c42776e \
    file://src/poetry/core/_vendor/tomlkit/LICENSE;md5=31aac0dbc1babd278d5386dadb7f8e82 \
    file://src/poetry/core/_vendor/typing_extensions.LICENSE;md5=64fc2b30b67d0a8423c250e0386ed72f \
"

SRC_URI[sha256sum] = "0ab006a40cb38d6a38b97264f6835da2f08a96912f2728ce668e9ac6a34f686f"

inherit python_poetry_core pypi

RDEPENDS:${PN}:append:class-target = "\
    python3-compression \
    python3-core \
    python3-crypt \
    python3-io \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-pathlib2 \
    python3-pprint \
    python3-shell \
"

RDEPENDS:${PN} += "\
    python3-pip \
    python3-six \
"

BBCLASSEXTEND = "native nativesdk"
