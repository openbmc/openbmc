require cmake.inc

inherit cmake

DEPENDS += "curl expat zlib libarchive xz ncurses bzip2"

SRC_URI_append_class-nativesdk = " \
    file://OEToolchainConfig.cmake \
    file://environment.d-cmake.sh"

# Strip ${prefix} from ${docdir}, set result into docdir_stripped
python () {
    prefix=d.getVar("prefix", True)
    docdir=d.getVar("docdir", True)

    if not docdir.startswith(prefix):
        raise bb.build.FuncFailed('docdir must contain prefix as its prefix')

    docdir_stripped = docdir[len(prefix):]
    if len(docdir_stripped) > 0 and docdir_stripped[0] == '/':
        docdir_stripped = docdir_stripped[1:]

    d.setVar("docdir_stripped", docdir_stripped)
}

EXTRA_OECMAKE=" \
    -DCMAKE_DOC_DIR=${docdir_stripped}/cmake-${CMAKE_MAJOR_VERSION} \
    -DCMAKE_USE_SYSTEM_LIBRARY_BZIP2=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_CURL=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_EXPAT=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_FORM=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBARCHIVE=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBLZMA=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_ZLIB=1 \
    -DKWSYS_CHAR_IS_SIGNED=1 \
    -DBUILD_CursesDialog=0 \
    ${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '-DKWSYS_LFS_WORKS=1', '-DKWSYS_LFS_DISABLE=1', d)} \
"

do_install_append_class-nativesdk() {
    mkdir -p ${D}${datadir}/cmake
    install -m 644 ${WORKDIR}/OEToolchainConfig.cmake ${D}${datadir}/cmake/

    mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
    install -m 644 ${WORKDIR}/environment.d-cmake.sh ${D}${SDKPATHNATIVE}/environment-setup.d/cmake.sh
}

FILES_${PN}_append_class-nativesdk = " ${SDKPATHNATIVE}"

FILES_${PN} += "${datadir}/cmake-${CMAKE_MAJOR_VERSION}"
FILES_${PN}-doc += "${docdir}/cmake-${CMAKE_MAJOR_VERSION}"

BBCLASSEXTEND = "nativesdk"
