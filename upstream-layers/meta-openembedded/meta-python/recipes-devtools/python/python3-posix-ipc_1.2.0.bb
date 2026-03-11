DESCRIPTION = "POSIX IPC primitives (semaphores, shared memory and message queues) for Python"
HOMEPAGE = "https://semanchuk.com/philip/posix_ipc/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a4f3bd729df04bf68f66ef877e9c7c9"

PYPI_PACKAGE = "posix_ipc"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "b7444e2703c156b3cb9fcb568e85d716232f3e78f04529ebc881cfb2aedb3838"

SRC_URI += " \
    file://0001-build_support-use-source-filename-instead-of-foo-for.patch \
    file://0002-build_support-handle-empty-max_priority-value-as-Non.patch \
    file://0003-build_support-use-does_build_succeed-in-compile_and_.patch \
"

# Message queue support requires librt for proper linking
LDFLAGS += "-lrt"

inherit pypi python_setuptools_build_meta
