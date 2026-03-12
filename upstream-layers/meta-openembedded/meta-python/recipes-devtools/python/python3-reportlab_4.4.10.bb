SUMMARY = "The Reportlab Toolkit."
DESCRIPTION = "An Open Source Python library for generating PDFs and graphics."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf24392f451ff6710fca1e96cefa0424"

SRC_URI[sha256sum] = "5cbbb34ac3546039d0086deb2938cdec06b12da3cdb836e813258eb33cd28487"

CVE_PRODUCT = "reportlab"
CVE_STATUS[CVE-2020-28463] = "fixed-version: has been fixed since 3.5.55"
inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
