SUMMARY = "A lil' TOML parser"
DESCRIPTION = "Tomli is a Python library for parsing TOML. Tomli is fully \
compatible with TOML v1.0.0."
HOMEPAGE = "https://github.com/hukkin/tomli"
BUGTRACKER = "https://github.com/hukkin/tomli/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5"

inherit pypi setuptools3

SRC_URI[sha256sum] = "c292c34f58502a1eb2bbb9f5bbc9a5ebc37bee10ffb8c2d6bbdfa8eb13cc14e1"

do_configure:prepend() {
cat > ${S}/setup.py <<-EOF
from setuptools import setup
setup(name="tomli", version="${PV}", packages=["tomli"], package_data={"": ["*"]})
EOF
}

BBCLASSEXTEND = "native nativesdk"
