SUMMARY = "Opencv : The Open Computer Vision Library"
HOMEPAGE = "http://opencv.org/"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ea90d28b4de883d7af5e6711f14f7bf"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

DEFAULT_PREFERENCE = "-1"

DEPENDS = "python-numpy libtool swig swig-native python bzip2 zlib glib-2.0 libwebp libgphoto2 protobuf protobuf-native"

SRCREV_opencv = "92387b1ef8fad15196dd5f7fb4931444a68bc93a"
SRCREV_contrib = "5409d5ad560523c85c6796cc5a009347072d883c"
SRCREV_party3 = "81a676001ca8075ada498583e4166079e5744668"
IPP_MD5 = "808b791a6eac9ed78d32a7666804320e"

SRCREV_FORMAT = "opencv"
SRC_URI = "git://github.com/Itseez/opencv.git;name=opencv \
            git://github.com/Itseez/opencv_contrib.git;destsuffix=contrib;name=contrib \
            git://github.com/Itseez/opencv_3rdparty.git;branch=ippicv/master_20151201;destsuffix=party3;name=party3 \
            file://0001-3rdparty-ippicv-Use-pre-downloaded-ipp.patch \
            file://fixpkgconfig.patch"

PV = "3.1+git${SRCPV}"

S = "${WORKDIR}/git"

do_unpack_extra() {
    tar xzf ${WORKDIR}/party3/ippicv/ippicv_linux_20151201.tgz -C ${WORKDIR}
}
addtask unpack_extra after do_unpack before do_patch

EXTRA_OECMAKE = "-DPYTHON2_NUMPY_INCLUDE_DIRS:PATH=${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/numpy/core/include \
		 -DOPENCV_EXTRA_MODULES_PATH=${WORKDIR}/contrib/modules \
                 -DWITH_1394=OFF \
                 -DCMAKE_SKIP_RPATH=ON \
                 -DOPENCV_ICV_PACKAGE_DOWNLOADED=${IPP_MD5} \
                 -DOPENCV_ICV_PATH=${WORKDIR}/ippicv_lnx \
                 ${@bb.utils.contains("TARGET_CC_ARCH", "-msse3", "-DENABLE_SSE=1 -DENABLE_SSE2=1 -DENABLE_SSE3=1 -DENABLE_SSSE3=1", "", d)} \
                 ${@bb.utils.contains("TARGET_CC_ARCH", "-msse4.1", "-DENABLE_SSE=1 -DENABLE_SSE2=1 -DENABLE_SSE3=1 -DENABLE_SSSE3=1 -DENABLE_SSE41=1", "", d)} \
                 ${@bb.utils.contains("TARGET_CC_ARCH", "-msse4.2", "-DENABLE_SSE=1 -DENABLE_SSE2=1 -DENABLE_SSE3=1 -DENABLE_SSSE3=1 -DENABLE_SSE41=1 -DENABLE_SSE42=1", "", d)} \
                 ${@base_conditional("libdir", "/usr/lib64", "-DLIB_SUFFIX=64", "", d)} \
                 ${@base_conditional("libdir", "/usr/lib32", "-DLIB_SUFFIX=32", "", d)} \
"
EXTRA_OECMAKE_append_x86 = " -DX86=ON"

PACKAGECONFIG ??= "eigen jpeg png tiff v4l libv4l gstreamer samples tbb \
                   ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gtk", "", d)} \
		   ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "libav", "", d)}"

PACKAGECONFIG[amdblas] = "-DWITH_OPENCLAMDBLAS=ON,-DWITH_OPENCLAMDBLAS=OFF,libclamdblas,"
PACKAGECONFIG[amdfft] = "-DWITH_OPENCLAMDFFT=ON,-DWITH_OPENCLAMDFFT=OFF,libclamdfft,"
PACKAGECONFIG[eigen] = "-DWITH_EIGEN=ON,-DWITH_EIGEN=OFF,libeigen,"
PACKAGECONFIG[gstreamer] = "-DWITH_GSTREAMER=ON,-DWITH_GSTREAMER=OFF,gstreamer1.0 gstreamer1.0-plugins-base,"
PACKAGECONFIG[gtk] = "-DWITH_GTK=ON,-DWITH_GTK=OFF,gtk+3,"
PACKAGECONFIG[jasper] = "-DWITH_JASPER=ON,-DWITH_JASPER=OFF,jasper,"
PACKAGECONFIG[jpeg] = "-DWITH_JPEG=ON,-DWITH_JPEG=OFF,jpeg,"
PACKAGECONFIG[libav] = "-DWITH_FFMPEG=ON,-DWITH_FFMPEG=OFF,libav,"
PACKAGECONFIG[libv4l] = "-DWITH_LIBV4L=ON,-DWITH_LIBV4L=OFF,v4l-utils,"
PACKAGECONFIG[opencl] = "-DWITH_OPENCL=ON,-DWITH_OPENCL=OFF,opencl-headers,"
PACKAGECONFIG[oracle-java] = "-DJAVA_INCLUDE_PATH=${JAVA_HOME}/include -DJAVA_INCLUDE_PATH2=${JAVA_HOME}/include/linux -DJAVA_AWT_INCLUDE_PATH=${JAVA_HOME}/include -DJAVA_AWT_LIBRARY=${JAVA_HOME}/lib/amd64/libjawt.so -DJAVA_JVM_LIBRARY=${JAVA_HOME}/lib/amd64/server/libjvm.so,,ant-native oracle-jse-jdk oracle-jse-jdk-native,"
PACKAGECONFIG[png] = "-DWITH_PNG=ON,-DWITH_PNG=OFF,libpng,"
PACKAGECONFIG[samples] = "-DBUILD_EXAMPLES=ON -DINSTALL_PYTHON_EXAMPLES=ON,-DBUILD_EXAMPLES=OFF,,"
PACKAGECONFIG[tbb] = "-DWITH_TBB=ON,-DWITH_TBB=OFF,tbb,"
PACKAGECONFIG[tiff] = "-DWITH_TIFF=ON,-DWITH_TIFF=OFF,tiff,"
PACKAGECONFIG[v4l] = "-DWITH_V4L=ON,-DWITH_V4L=OFF,v4l-utils,"

inherit distutils-base pkgconfig cmake

export BUILD_SYS
export HOST_SYS
export PYTHON_CSPEC="-I${STAGING_INCDIR}/${PYTHON_DIR}"
export PYTHON="${STAGING_BINDIR_NATIVE}/python"
export JAVA_HOME="${STAGING_DIR_NATIVE}/usr/bin/java"
export ANT_DIR="${STAGING_DIR_NATIVE}/usr/share/ant/"

TARGET_CC_ARCH += "-I${S}/include "

PACKAGES += "${PN}-samples-dbg ${PN}-samples ${PN}-apps python-opencv \
             ${@bb.utils.contains('PACKAGECONFIG', 'oracle-java', '${PN}-java-dbg ${PN}-java', '', d)}"

python populate_packages_prepend () {
    cv_libdir = d.expand('${libdir}')
    cv_libdir_dbg = d.expand('${libdir}/.debug')
    do_split_packages(d, cv_libdir, '^lib(.*)\.so$', 'lib%s-dev', 'OpenCV %s development package', extra_depends='${PN}-dev', allow_links=True)
    do_split_packages(d, cv_libdir, '^lib(.*)\.la$', 'lib%s-dev', 'OpenCV %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, cv_libdir, '^lib(.*)\.a$', 'lib%s-dev', 'OpenCV %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, cv_libdir, '^lib(.*)\.so\.*', 'lib%s', 'OpenCV %s library', extra_depends='', allow_links=True)

    pn = d.getVar('PN', 1)
    metapkg =  pn + '-dev'
    d.setVar('ALLOW_EMPTY_' + metapkg, "1")
    blacklist = [ metapkg ]
    metapkg_rdepends = [ ]
    packages = d.getVar('PACKAGES', 1).split()
    for pkg in packages[1:]:
        if not pkg in blacklist and not pkg in metapkg_rdepends and pkg.endswith('-dev'):
            metapkg_rdepends.append(pkg)
    d.setVar('RRECOMMENDS_' + metapkg, ' '.join(metapkg_rdepends))

    metapkg =  pn
    blacklist = [ metapkg ]
    metapkg_rdepends = [ ]
    for pkg in packages[1:]:
        if not pkg in blacklist and not pkg in metapkg_rdepends and not pkg.endswith('-dev') and not pkg.endswith('-dbg') and not pkg.endswith('-doc') and not pkg.endswith('-locale'):
            metapkg_rdepends.append(pkg)
    bb.data.setVar('RDEPENDS_' + metapkg, ' '.join(metapkg_rdepends), d)

}

PACKAGES_DYNAMIC += "^libopencv-.*"

FILES_${PN} = ""
FILES_${PN}-apps = "${bindir}/* ${datadir}/OpenCV"
FILES_${PN}-dbg += "${libdir}/.debug"
FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig"
FILES_${PN}-doc = "${datadir}/OpenCV/doc"
FILES_${PN}-java = "${datadir}/OpenCV/java"
FILES_${PN}-java-dbg = "${datadir}/OpenCV/java/.debug/"
FILES_${PN}-samples = "${datadir}/OpenCV/samples/"
FILES_${PN}-samples-dbg = "${datadir}/OpenCV/samples/bin/.debug"

INSANE_SKIP_${PN}-apps = "staticdev"
INSANE_SKIP_${PN}-java = "libdir"
INSANE_SKIP_${PN}-java-dbg = "libdir"

ALLOW_EMPTY_${PN} = "1"

SUMMARY_python-opencv = "Python bindings to opencv"
FILES_python-opencv = "${PYTHON_SITEPACKAGES_DIR}/*"
RDEPENDS_python-opencv = "python-core python-numpy"

do_install_append() {
    cp ${S}/include/opencv/*.h ${D}${includedir}/opencv/
    sed -i '/blobtrack/d' ${D}${includedir}/opencv/cvaux.h

    # Move Python files into correct library folder (for multilib build)
    if [ "$libdir" != "/usr/lib" ]; then
        mv ${D}/usr/lib/* ${D}/${libdir}/
        rm -rf ${D}/usr/lib
    fi

    install -d ${D}${datadir}/OpenCV/samples/bin/
    cp -f bin/*-tutorial-* bin/*-example-* ${D}${datadir}/OpenCV/samples/bin/
}

