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
    file://src/poetry/core/_vendor/lark/LICENSE;md5=fcfbf1e2ecc0f37acbb5871aa0267500 \
    file://src/poetry/core/_vendor/packaging/LICENSE;md5=faadaedca9251a90b205c9167578ce91 \
    file://src/poetry/core/_vendor/packaging/LICENSE.APACHE;md5=2ee41112a44fe7014dce33e26468ba93 \
    file://src/poetry/core/_vendor/packaging/LICENSE.BSD;md5=7bef9bf4a8e4263634d0597e7ba100b8 \
    file://src/poetry/core/_vendor/pyrsistent/LICENSE.mit;md5=b695eb9c6e7a6fb1b1bc2d193c42776e \
    file://src/poetry/core/_vendor/tomlkit/LICENSE;md5=31aac0dbc1babd278d5386dadb7f8e82 \
    file://src/poetry/core/_vendor/typing_extensions.LICENSE;md5=f16b323917992e0f8a6f0071bc9913e2 \
"

SRC_URI[sha256sum] = "c6556c3b1ec5b8668e6ef5a4494726bc41d31907339425e194e78a6178436c14"

inherit python_poetry_core pypi
PYPI_ARCHIVE_NAME = "poetry_core-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/poetry_core-${PV}"

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
