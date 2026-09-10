# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Fastest Python implementation of JSON schema"
HOMEPAGE = "https://github.com/seznam/python-fastjsonschema"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18950e8362b69c0c617b42b8bd8e7532"

SRC_URI[sha256sum] = "72064e12356a7d6ef02165be2946b9abadbdf238536e07eb587e3dbaa33099cf"

inherit ptest-python-pytest pypi setuptools3

# python3-misc for timeit.py
RDEPENDS:${PN}-ptest += "\
    python3-colorama \
    python3-jsonschema \
    python3-misc \
    python3-pylint \
    python3-pytest-benchmark \
    python3-statistics \
"
RDEPENDS:${PN} += "\
    python3-core \
    python3-urllib3 \
    python3-numbers \
    python3-pickle \
    python3-json \
    "

BBCLASSEXTEND = "native nativesdk"
