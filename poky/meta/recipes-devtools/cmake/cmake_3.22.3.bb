require cmake.inc

inherit cmake bash-completion

DEPENDS += "curl expat zlib libarchive xz ncurses bzip2"

SRC_URI:append:class-nativesdk = " \
    file://OEToolchainConfig.cmake \
    file://SDKToolchainConfig.cmake.template \
    file://cmake-setup.py \
    file://environment.d-cmake.sh \
    file://0001-CMakeDetermineSystem-use-oe-environment-vars-to-load.patch \
"

LICENSE:append = " & BSD-1-Clause & MIT"
LIC_FILES_CHKSUM:append = " \
    file://Utilities/cmjsoncpp/LICENSE;md5=fa2a23dd1dc6c139f35105379d76df2b \
    file://Utilities/cmlibrhash/COPYING;md5=a8c2a557a5c53b1c12cddbee98c099af \
    file://Utilities/cmlibuv/LICENSE;md5=a68902a430e32200263d182d44924d47 \
"

# Strip ${prefix} from ${docdir}, set result into docdir_stripped
python () {
    prefix=d.getVar("prefix")
    docdir=d.getVar("docdir")

    if not docdir.startswith(prefix):
        bb.fatal('docdir must contain prefix as its prefix')

    docdir_stripped = docdir[len(prefix):]
    if len(docdir_stripped) > 0 and docdir_stripped[0] == '/':
        docdir_stripped = docdir_stripped[1:]

    d.setVar("docdir_stripped", docdir_stripped)
}

EXTRA_OECMAKE=" \
    -DCMAKE_DOC_DIR=${docdir_stripped}/cmake-${CMAKE_MAJOR_VERSION} \
    -DCMAKE_USE_SYSTEM_LIBRARIES=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_JSONCPP=0 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBUV=0 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBRHASH=0 \
    -DKWSYS_CHAR_IS_SIGNED=1 \
    -DBUILD_CursesDialog=0 \
    -DKWSYS_LFS_WORKS=1 \
"

do_install:append:class-nativesdk() {
    mkdir -p ${D}${datadir}/cmake
    install -m 644 ${WORKDIR}/OEToolchainConfig.cmake ${D}${datadir}/cmake/

    mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
    install -m 644 ${WORKDIR}/environment.d-cmake.sh ${D}${SDKPATHNATIVE}/environment-setup.d/cmake.sh

    # install cmake-setup.py to create arch-specific toolchain cmake file from template
    install -m 0644 ${WORKDIR}/SDKToolchainConfig.cmake.template ${D}${datadir}/cmake/
    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/cmake-setup.py ${D}${SDKPATHNATIVE}/post-relocate-setup.d/
}

FILES:${PN}:append:class-nativesdk = " ${SDKPATHNATIVE}"

FILES:${PN} += "${datadir}/cmake-${CMAKE_MAJOR_VERSION} ${datadir}/cmake ${datadir}/aclocal ${datadir}/emacs ${datadir}/vim"
FILES:${PN}-doc += "${docdir}/cmake-${CMAKE_MAJOR_VERSION}"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "nativesdk"
