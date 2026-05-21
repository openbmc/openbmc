SUMMARY = "The Reportlab Toolkit."
DESCRIPTION = "An Open Source Python library for generating PDFs and graphics."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf24392f451ff6710fca1e96cefa0424"

SRC_URI[sha256sum] = "e595932789ab7a107ba253e83f7815622708a9fd49920d0d6a909880eb66ac75"

CVE_PRODUCT = "reportlab"
CVE_STATUS[CVE-2020-28463] = "fixed-version: has been fixed since 3.5.55"
inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
