SUMMARY = "Smart, pythonic, ad-hoc, typed polymorphism for Python."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=155fbcc756c8ae5265d252d23e20908f"

SRC_URI[sha256sum] = "4de4fdd6c5c38607bbd8ad76703d7cc4dbe007cfa78e8ef1f62fc6ac55303e23"

inherit pypi python_poetry_core

RDEPENDS:${PN} += "python3-typing-extensions"
