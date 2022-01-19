DESCRIPTION = "POSIX IPC primitives (semaphores, shared memory and message queues) for Python"
HOMEPAGE = "http://semanchuk.com/philip/posix_ipc/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc089fb2f37e90799a54c19a20c9880f"

PYPI_PACKAGE = "posix_ipc"

# Archived version so we need to overwrite what the pypi class will derive
PYPI_SRC_URI = "https://pypi.python.org/packages/source/p/${PYPI_PACKAGE}/${PYPI_PACKAGE}-${PV}.tar.gz"

SRC_URI[md5sum] = "8c9443859492ecf3aae9182aa6b5c78c"
SRC_URI[sha256sum] = "6cddb1ce2cf4aae383f2a0079c26c69bee257fe2720f372201ef047f8ceb8b97"

inherit setuptools3 pypi
