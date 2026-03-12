SUMMARY = "Multiprocess extends multiprocessing to provide enhanced serialization, using dill."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4e77a25170a36f151649917fe8d2858e"

SRC_URI[sha256sum] = "952021e0e6c55a4a9fe4cd787895b86e239a40e76802a789d6305398d3975897"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-dill"
