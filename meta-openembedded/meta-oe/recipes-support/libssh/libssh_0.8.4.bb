SUMMARY = "Multiplatform C library implementing the SSHv2 and SSHv1 protocol"
HOMEPAGE = "http://www.libssh.org"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=388a4fb1dea8ceae0be78ba9b01fc139"

DEPENDS = "zlib openssl libgcrypt"

SRC_URI = "git://git.libssh.org/projects/libssh.git;branch=stable-0.8"
SRCREV = "789df0b7d0c7abd6b85db9fc5247e146e3d4ddba"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??=""
PACKAGECONFIG[gssapi] = "-DWITH_GSSAPI=1, -DWITH_GSSAPI=0, krb5, "

ARM_INSTRUCTION_SET_armv5 = "arm"

EXTRA_OECMAKE = " \
    -DWITH_GCRYPT=1 \
    -DWITH_PCAP=1 \
    -DWITH_SFTP=1 \
    -DWITH_ZLIB=1 \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
    "

do_configure_prepend () {
    # Disable building of examples
    sed -i -e '/add_subdirectory(examples)/s/^/#DONOTWANT/' ${S}/CMakeLists.txt \
        || bbfatal "Failed to disable examples"
}

TOOLCHAIN = "gcc"
