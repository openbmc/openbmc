SUMMARY = "Opencv : The Open Computer Vision Library"
HOMEPAGE = "http://opencv.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

DEPENDS = "libtool swig-native bzip2 zlib glib-2.0 libwebp"

SRCREV_opencv = "69357b1e88680658a07cffde7678a4d697469f03"
SRCREV_contrib = "f5d7f6712d4ff229ba4f45cf79dfd11c557d56fd"
SRCREV_ipp = "a56b6ac6f030c312b2dce17430eef13aed9af274"
SRCREV_boostdesc = "34e4206aef44d50e6bbcd0ab06354b52e7466d26"
SRCREV_vgg = "fccf7cd6a4b12079f73bbfb21745f9babcd4eb1d"
SRCREV_face = "8afa57abc8229d611c4937165d20e2a2d9fc5a12"
SRCREV_wechat-qrcode = "a8b69ccc738421293254aec5ddb38bd523503252"

def ipp_filename(d):
    import re
    arch = d.getVar('TARGET_ARCH')
    if re.match("i.86$", arch):
        return "ippicv_2020_lnx_ia32_20191018_general.tgz"
    else:
        return "ippicv_2020_lnx_intel64_20191018_general.tgz"

def ipp_md5sum(d):
    import re
    arch = d.getVar('TARGET_ARCH')
    if re.match("i.86$", arch):
        return "ad189a940fb60eb71f291321322fe3e8"
    else:
        return "7421de0095c7a39162ae13a6098782f9"

IPP_FILENAME = "${@ipp_filename(d)}"
IPP_MD5 = "${@ipp_md5sum(d)}"

SRCREV_FORMAT = "opencv_contrib_ipp_boostdesc_vgg"
SRC_URI = "git://github.com/opencv/opencv.git;name=opencv \
           git://github.com/opencv/opencv_contrib.git;destsuffix=contrib;name=contrib \
           git://github.com/opencv/opencv_3rdparty.git;branch=ippicv/master_20191018;destsuffix=ipp;name=ipp \
           git://github.com/opencv/opencv_3rdparty.git;branch=contrib_xfeatures2d_boostdesc_20161012;destsuffix=boostdesc;name=boostdesc \
           git://github.com/opencv/opencv_3rdparty.git;branch=contrib_xfeatures2d_vgg_20160317;destsuffix=vgg;name=vgg \
           git://github.com/opencv/opencv_3rdparty.git;branch=contrib_face_alignment_20170818;destsuffix=face;name=face \
           git://github.com/WeChatCV/opencv_3rdparty.git;branch=wechat_qrcode;destsuffix=wechat_qrcode;name=wechat-qrcode \
           file://0001-3rdparty-ippicv-Use-pre-downloaded-ipp.patch \
           file://0003-To-fix-errors-as-following.patch \
           file://0001-Temporarliy-work-around-deprecated-ffmpeg-RAW-functi.patch \
           file://0001-Dont-use-isystem.patch \
           file://download.patch \
           file://0001-Make-ts-module-external.patch \
           file://0001-sfm-link-with-Glog_LIBS.patch;patchdir=../contrib \
           "
SRC_URI_append_riscv64 = " file://0001-Use-Os-to-compile-tinyxml2.cpp.patch;patchdir=../contrib"

S = "${WORKDIR}/git"

# OpenCV wants to download more files during configure.  We download these in
# do_fetch and construct a source cache in the format it expects
OPENCV_DLDIR = "${WORKDIR}/downloads"

do_unpack_extra() {
    tar xzf ${WORKDIR}/ipp/ippicv/${IPP_FILENAME} -C ${WORKDIR}

    md5() {
        # Return the MD5 of $1
        echo $(md5sum $1 | cut -d' ' -f1)
    }
    cache() {
        TAG=$1
        shift
        mkdir --parents ${OPENCV_DLDIR}/$TAG
        for F in $*; do
            DEST=${OPENCV_DLDIR}/$TAG/$(md5 $F)-$(basename $F)
            test -e $DEST || ln -s $F $DEST
        done
    }
    cache xfeatures2d/boostdesc ${WORKDIR}/boostdesc/*.i
    cache xfeatures2d/vgg ${WORKDIR}/vgg/*.i
    cache data ${WORKDIR}/face/*.dat
    cache wechat_qrcode ${WORKDIR}/wechat_qrcode/*.caffemodel
    cache wechat_qrcode ${WORKDIR}/wechat_qrcode/*.prototxt
}
addtask unpack_extra after do_unpack before do_patch

CMAKE_VERBOSE = "VERBOSE=1"

EXTRA_OECMAKE = "-DOPENCV_EXTRA_MODULES_PATH=${WORKDIR}/contrib/modules \
    -DWITH_1394=OFF \
    -DENABLE_PRECOMPILED_HEADERS=OFF \
    -DCMAKE_SKIP_RPATH=ON \
    -DOPENCV_ICV_HASH=${IPP_MD5} \
    -DIPPROOT=${WORKDIR}/ippicv_lnx \
    -DOPENCV_GENERATE_PKGCONFIG=ON \
    -DOPENCV_DOWNLOAD_PATH=${OPENCV_DLDIR} \
    -DOPENCV_ALLOW_DOWNLOADS=OFF \
    ${@bb.utils.contains("TARGET_CC_ARCH", "-msse3", "-DENABLE_SSE=1 -DENABLE_SSE2=1 -DENABLE_SSE3=1 -DENABLE_SSSE3=1", "", d)} \
    ${@bb.utils.contains("TARGET_CC_ARCH", "-msse4.1", "-DENABLE_SSE=1 -DENABLE_SSE2=1 -DENABLE_SSE3=1 -DENABLE_SSSE3=1 -DENABLE_SSE41=1", "", d)} \
    ${@bb.utils.contains("TARGET_CC_ARCH", "-msse4.2", "-DENABLE_SSE=1 -DENABLE_SSE2=1 -DENABLE_SSE3=1 -DENABLE_SSSE3=1 -DENABLE_SSE41=1 -DENABLE_SSE42=1", "", d)} \
"
EXTRA_OECMAKE_append_x86 = " -DX86=ON"

PACKAGECONFIG ??= "gapi python3 eigen jpeg png tiff v4l libv4l gstreamer samples tbb gphoto2 \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gtk", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "libav", "", d)}"

# TBB does not build for powerpc so disable that package config
PACKAGECONFIG_remove_powerpc = "tbb"
# tbb now needs getcontect/setcontext which is not there for all arches on musl
PACKAGECONFIG_remove_libc-musl_riscv64 = "tbb"
PACKAGECONFIG_remove_libc-musl_riscv32 = "tbb"

PACKAGECONFIG[gapi] = "-DWITH_ADE=ON -Dade_DIR=${STAGING_LIBDIR},-DWITH_ADE=OFF,ade"
PACKAGECONFIG[amdblas] = "-DWITH_OPENCLAMDBLAS=ON,-DWITH_OPENCLAMDBLAS=OFF,libclamdblas,"
PACKAGECONFIG[amdfft] = "-DWITH_OPENCLAMDFFT=ON,-DWITH_OPENCLAMDFFT=OFF,libclamdfft,"
PACKAGECONFIG[dnn] = "-DBUILD_opencv_dnn=ON -DPROTOBUF_UPDATE_FILES=ON -DBUILD_PROTOBUF=OFF,-DBUILD_opencv_dnn=OFF,protobuf protobuf-native,"
PACKAGECONFIG[eigen] = "-DWITH_EIGEN=ON,-DWITH_EIGEN=OFF,libeigen gflags glog,"
PACKAGECONFIG[freetype] = "-DBUILD_opencv_freetype=ON,-DBUILD_opencv_freetype=OFF,freetype,"
PACKAGECONFIG[gphoto2] = "-DWITH_GPHOTO2=ON,-DWITH_GPHOTO2=OFF,libgphoto2,"
PACKAGECONFIG[gstreamer] = "-DWITH_GSTREAMER=ON,-DWITH_GSTREAMER=OFF,gstreamer1.0 gstreamer1.0-plugins-base,"
PACKAGECONFIG[gtk] = "-DWITH_GTK=ON,-DWITH_GTK=OFF,gtk+3,"
PACKAGECONFIG[jasper] = "-DWITH_JASPER=ON,-DWITH_JASPER=OFF,jasper,"
PACKAGECONFIG[java] = "-DJAVA_INCLUDE_PATH=${JAVA_HOME}/include -DJAVA_INCLUDE_PATH2=${JAVA_HOME}/include/linux -DJAVA_AWT_INCLUDE_PATH=${JAVA_HOME}/include -DJAVA_AWT_LIBRARY=${JAVA_HOME}/lib/amd64/libjawt.so -DJAVA_JVM_LIBRARY=${JAVA_HOME}/lib/amd64/server/libjvm.so,,ant-native fastjar-native openjdk-8-native,"
PACKAGECONFIG[jpeg] = "-DWITH_JPEG=ON,-DWITH_JPEG=OFF,jpeg,"
PACKAGECONFIG[libav] = "-DWITH_FFMPEG=ON,-DWITH_FFMPEG=OFF,libav,"
PACKAGECONFIG[libv4l] = "-DWITH_LIBV4L=ON,-DWITH_LIBV4L=OFF,v4l-utils,"
PACKAGECONFIG[opencl] = "-DWITH_OPENCL=ON,-DWITH_OPENCL=OFF,opencl-headers virtual/opencl-icd,"
PACKAGECONFIG[oracle-java] = "-DJAVA_INCLUDE_PATH=${ORACLE_JAVA_HOME}/include -DJAVA_INCLUDE_PATH2=${ORACLE_JAVA_HOME}/include/linux -DJAVA_AWT_INCLUDE_PATH=${ORACLE_JAVA_HOME}/include -DJAVA_AWT_LIBRARY=${ORACLE_JAVA_HOME}/lib/amd64/libjawt.so -DJAVA_JVM_LIBRARY=${ORACLE_JAVA_HOME}/lib/amd64/server/libjvm.so,,ant-native oracle-jse-jdk oracle-jse-jdk-native,"
PACKAGECONFIG[png] = "-DWITH_PNG=ON,-DWITH_PNG=OFF,libpng,"
PACKAGECONFIG[python2] = "-DPYTHON2_NUMPY_INCLUDE_DIRS:PATH=${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/numpy/core/include,,python-numpy,"
PACKAGECONFIG[python3] = "-DPYTHON3_NUMPY_INCLUDE_DIRS:PATH=${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/numpy/core/include,,python3-numpy,"
PACKAGECONFIG[samples] = "-DBUILD_EXAMPLES=ON -DINSTALL_PYTHON_EXAMPLES=ON,-DBUILD_EXAMPLES=OFF,,"
PACKAGECONFIG[tbb] = "-DWITH_TBB=ON,-DWITH_TBB=OFF,tbb,"
PACKAGECONFIG[tests] = "-DBUILD_TESTS=ON,-DBUILD_TESTS=OFF,,"
PACKAGECONFIG[text] = "-DBUILD_opencv_text=ON,-DBUILD_opencv_text=OFF,tesseract,"
PACKAGECONFIG[tiff] = "-DWITH_TIFF=ON,-DWITH_TIFF=OFF,tiff,"
PACKAGECONFIG[v4l] = "-DWITH_V4L=ON,-DWITH_V4L=OFF,v4l-utils,"

inherit pkgconfig cmake

inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'distutils3-base', '', d)}
inherit ${@bb.utils.contains('PACKAGECONFIG', 'python2', 'distutils-base', '', d)}

export PYTHON_CSPEC="-I${STAGING_INCDIR}/${PYTHON_DIR}"
export PYTHON="${STAGING_BINDIR_NATIVE}/${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3', 'python', d)}"
export ORACLE_JAVA_HOME="${STAGING_DIR_NATIVE}/usr/bin/java"
export JAVA_HOME="${STAGING_DIR_NATIVE}/usr/lib/jvm/openjdk-8-native"
export ANT_DIR="${STAGING_DIR_NATIVE}/usr/share/ant/"

TARGET_CC_ARCH += "-I${S}/include "

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'samples', '${PN}-samples', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'oracle-java', '${PN}-java', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'java', '${PN}-java', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'python2', 'python-${BPN}', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3-${BPN}', '', d)} \
    ${PN}-apps"

python populate_packages_prepend () {
    cv_libdir = d.expand('${libdir}')
    do_split_packages(d, cv_libdir, '^lib(.*)\.so$', 'lib%s-dev', 'OpenCV %s development package', extra_depends='${PN}-dev', allow_links=True)
    do_split_packages(d, cv_libdir, '^lib(.*)\.la$', 'lib%s-dev', 'OpenCV %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, cv_libdir, '^lib(.*)\.a$', 'lib%s-dev', 'OpenCV %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, cv_libdir, '^lib(.*)\.so\.*', 'lib%s', 'OpenCV %s library', extra_depends='', allow_links=True)

    pn = d.getVar('PN')
    metapkg =  pn + '-dev'
    d.setVar('ALLOW_EMPTY_' + metapkg, "1")
    blacklist = [ metapkg ]
    metapkg_rdepends = [ ]
    packages = d.getVar('PACKAGES').split()
    for pkg in packages[1:]:
        if not pkg in blacklist and not pkg in metapkg_rdepends and pkg.endswith('-dev'):
            metapkg_rdepends.append(pkg)
    d.setVar('RRECOMMENDS_' + metapkg, ' '.join(metapkg_rdepends))

    metapkg =  pn
    d.setVar('ALLOW_EMPTY_' + metapkg, "1")
    blacklist = [ metapkg, "libopencv-ts" ]
    metapkg_rdepends = [ ]
    for pkg in packages[1:]:
        if not pkg in blacklist and not pkg in metapkg_rdepends and not pkg.endswith('-dev') and not pkg.endswith('-dbg') and not pkg.endswith('-doc') and not pkg.endswith('-locale') and not pkg.endswith('-staticdev'):
            metapkg_rdepends.append(pkg)
    d.setVar('RDEPENDS_' + metapkg, ' '.join(metapkg_rdepends))
}

PACKAGES_DYNAMIC += "^libopencv-.*"

FILES_${PN} = ""
FILES_${PN}-dbg += "${datadir}/OpenCV/java/.debug/* ${datadir}/OpenCV/samples/bin/.debug/*"
FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig  ${libdir}/cmake/opencv4/*.cmake"
FILES_${PN}-staticdev += "${libdir}/opencv4/3rdparty/*.a"
FILES_${PN}-apps = "${bindir}/* ${datadir}/opencv4 ${datadir}/licenses"
FILES_${PN}-java = "${datadir}/OpenCV/java"
FILES_${PN}-samples = "${datadir}/opencv4/samples/"

INSANE_SKIP_${PN}-java = "libdir"
INSANE_SKIP_${PN}-dbg = "libdir"

ALLOW_EMPTY_${PN} = "1"

SUMMARY_python-opencv = "Python bindings to opencv"
FILES_python-opencv = "${PYTHON_SITEPACKAGES_DIR}/*"
RDEPENDS_python-opencv = "python-core python-numpy"

SUMMARY_python3-opencv = "Python bindings to opencv"
FILES_python3-opencv = "${PYTHON_SITEPACKAGES_DIR}/*"
RDEPENDS_python3-opencv = "python3-core python3-numpy"

RDEPENDS_${PN}-apps  = "bash"

do_compile_prepend() {
    # remove the build host info to improve reproducibility
    if [ -f ${WORKDIR}/build/modules/core/version_string.inc ]; then
        sed -i "s#${WORKDIR}#/workdir#g" ${WORKDIR}/build/modules/core/version_string.inc
    fi
}

do_install_append() {
    # Move Python files into correct library folder (for multilib build)
    if [ "$libdir" != "/usr/lib" -a -d ${D}/usr/lib ]; then
        mv ${D}/usr/lib/* ${D}/${libdir}/
        rm -rf ${D}/usr/lib
    fi
    # remove build host path to improve reproducibility
    if [ -f ${D}${libdir}/cmake/opencv4/OpenCVModules.cmake ]; then
        sed -e 's@${STAGING_DIR_HOST}@@g' \
            -i ${D}${libdir}/cmake/opencv4/OpenCVModules.cmake
    fi
}
