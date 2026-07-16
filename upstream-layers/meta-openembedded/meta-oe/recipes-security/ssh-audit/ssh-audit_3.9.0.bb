SUMMARY = "An SSH server and client configuration auditing tool"
HOMEPAGE = "https://github.com/jtesta/ssh-audit"
BUGTRACKER = "https://github.com/jtesta/ssh-audit/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2638d424730ffece235019c6a623c6fa"

SRC_URI[sha256sum] = "f1225d0364b3cb61c7dfb1f5065a6958dbb814d98b2c1dd2a779ba2cdef41f61"

PYPI_PACKAGE = "ssh_audit"

inherit pypi python_setuptools_build_meta
