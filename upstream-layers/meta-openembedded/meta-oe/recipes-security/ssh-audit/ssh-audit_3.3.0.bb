SUMMARY = "An SSH server and client configuration auditing tool"
HOMEPAGE = "https://github.com/jtesta/ssh-audit"
BUGTRACKER = "https://github.com/jtesta/ssh-audit/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8745b0cfd015df0e3199253390de8935"

SRC_URI[sha256sum] = "b76e36ac9844f45d64986c9f293a4b46766a10412dc29fb43bd52d0f6661a5b0"

PYPI_PACKAGE = "ssh_audit"

inherit pypi python_setuptools_build_meta
