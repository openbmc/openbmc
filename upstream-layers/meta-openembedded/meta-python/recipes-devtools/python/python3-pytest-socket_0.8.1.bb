SUMMARY = "Pytest Plugin to disable socket calls during tests"
HOMEPAGE = "https://github.com/miketheman/pytest-socket"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1752f63a3a8a02fed42e580e9b94a081"

SRC_URI += "file://0001-pyproject.toml-allow-uv_build-0.13.patch"
SRC_URI[sha256sum] = "2f57787914ad2e1308d09ce141b95c3e55741fbb4fb7b7556593a6b063e0c9c7"

inherit pypi python_uv_build

RDEPENDS:${PN} = "python3-pytest"

BBCLASSEXTEND = "native nativesdk"
