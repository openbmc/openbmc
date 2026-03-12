DESCRIPTION = "POSIX IPC primitives (semaphores, shared memory and message queues) for Python"
HOMEPAGE = "https://semanchuk.com/philip/posix_ipc/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3d8df223c2614dbf1aabdc1ca23cc10"

PYPI_PACKAGE = "posix_ipc"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "6923232111329954a8349f7d99f212b6e96b5206e77fbd39aaf1b3cb4a5e9260"

# Message queue support requires librt for proper linking
LDFLAGS += "-lrt"

inherit pypi python_setuptools_build_meta
