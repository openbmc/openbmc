SUMMARY = "Multiplatform C library implementing the SSHv2 and SSHv1 protocol"
HOMEPAGE = "http://www.libssh.org"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=dabb4958b830e5df11d2b0ed8ea255a0"

DEPENDS = "zlib openssl"

SRC_URI = "git://git.libssh.org/projects/libssh.git;protocol=https;branch=stable-0.8"
SRCREV = "04685a74df9ce1db1bc116a83a0da78b4f4fa1f8"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= "gcrypt"
PACKAGECONFIG[gssapi] = "-DWITH_GSSAPI=1, -DWITH_GSSAPI=0, krb5, "
PACKAGECONFIG[gcrypt] = "-DWITH_GCRYPT=1, -DWITH_GCRYPT=0, libgcrypt, "

ARM_INSTRUCTION_SET:armv5 = "arm"

EXTRA_OECMAKE = " \
    -DWITH_PCAP=1 \
    -DWITH_SFTP=1 \
    -DWITH_ZLIB=1 \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
    "

do_configure:prepend () {
    # Disable building of examples
    sed -i -e '/add_subdirectory(examples)/s/^/#DONOTWANT/' ${S}/CMakeLists.txt \
        || bbfatal "Failed to disable examples"
}

TOOLCHAIN = "gcc"

BBCLASSEXTEND = "native nativesdk"
