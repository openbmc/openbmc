DESCRIPTION = "POSIX IPC primitives (semaphores, shared memory and message queues) for Python"
HOMEPAGE = "http://semanchuk.com/philip/posix_ipc/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=513d94a7390d4d72f3475e2d45c739b5"

PYPI_PACKAGE = "posix_ipc"

SRC_URI[sha256sum] = "e2456ba0cfb2ee5ba14121450e8d825b3c4a1461fca0761220aab66d4111cbb7"

inherit setuptools3 pypi
