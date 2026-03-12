DESCRIPTION = "FreeRDP RDP client & server library"
HOMEPAGE = "http://www.freerdp.com"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl libusb1 uriparser cairo icu pkcs11-helper zlib jpeg json-c"
RDEPENDS:${PN}-ptest += "cmake coreutils"

inherit pkgconfig cmake ptest

SRCREV = "e3ef4c71138f76516299cb3637d2d0c59b2a3737"
SRC_URI = "git://github.com/FreeRDP/FreeRDP.git;branch=master;protocol=https;tag=${PV} \
           file://run-ptest"


CVE_PRODUCT = "freerdp"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pam pulseaudio wayland x11', d)} \
    ${@bb.utils.contains('LICENSE_FLAGS_ACCEPTED', 'commercial', 'ffmpeg', '', d)} \
    gstreamer cups pcsc \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'test', '', d)} \
"

EXTRA_OECMAKE = " \
    -DRDTK_FORCE_STATIC_BUILD=ON \
    -DUWAC_FORCE_STATIC_BUILD=ON \
    -DWITH_AAD=ON \
    -DWITH_BINARY_VERSIONING=ON \
    -DWITH_CHANNELS=ON \
    -DWITH_CLIENT_CHANNELS=ON \
    -DWITH_JPEG=ON \
    -DWITH_PKCS11=ON \
    -DWITH_SERVER_CHANNELS=ON \
    -DWITH_SERVER=ON \
    -DPKG_CONFIG_RELOCATABLE=OFF \
    -DWITH_ALSA=OFF \
    -DWITH_CLIENT_SDL=OFF \
    -DWITH_SAMPLE=OFF \
    -DWITH_CAIRO=ON \
    -DWITH_MANPAGES=OFF \
 "

X11_DEPS = "virtual/libx11 libxinerama libxext libxcursor libxv libxi libxrender libxfixes libxdamage libxrandr libxkbfile"
PACKAGECONFIG[x11] = "-DWITH_X11=ON -DWITH_XINERAMA=ON -DWITH_XEXT=ON -DWITH_XCURSOR=ON -DWITH_XV=ON -DWITH_XI=ON -DWITH_XRENDER=ON -DWITH_XFIXES=ON -DWITH_XDAMAGE=ON -DWITH_XRANDR=ON -DWITH_XKBFILE=ON,-DWITH_X11=OFF -DWITH_SHADOW=OFF,${X11_DEPS}"
PACKAGECONFIG[wayland] = "-DWITH_WAYLAND=ON,-DWITH_WAYLAND=OFF,wayland wayland-native libxkbcommon"
PACKAGECONFIG[pam] = "-DWITH_PAM=ON,-DWITH_PAM=OFF,libpam"
PACKAGECONFIG[pulseaudio] = "-DWITH_PULSEAUDIO=ON,-DWITH_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[gstreamer] = "-DWITH_GSTREAMER_1_0=ON,-DWITH_GSTREAMER_1_0=OFF,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[cups] = "-DWITH_CUPS=ON,-DWITH_CUPS=OFF,cups"
PACKAGECONFIG[fuse] = "-DWITH_FUSE=ON,-DWITH_FUSE=OFF,fuse3,fuse3"
PACKAGECONFIG[pcsc] = "-DWITH_PCSC=ON,-DWITH_PCSC=OFF,pcsc-lite"
PACKAGECONFIG[ffmpeg] = "-DWITH_DSP_FFMPEG=ON -DWITH_FFMPEG=ON -DWITH_SWSCALE=ON, -DWITH_DSP_FFMPEG=OFF -DWITH_FFMPEG=OFF -DWITH_SWSCALE=OFF,ffmpeg"
PACKAGECONFIG[krb5] = "-DWITH_KRB5=ON -DWITH_KRB5_NO_NTLM_FALLBACK=OFF,-DWITH_KRB5=OFF,krb5"
PACKAGECONFIG[openh264] = "-DWITH_OPENH264=ON,-DWITH_OPENH264=OFF,openh264"
PACKAGECONFIG[opencl] = "-DWITH_OPENCL=ON,-DWITH_OPENCL=OFF,virtual/libopencl1"
PACKAGECONFIG[lame] = "-DWITH_LAME=ON,-DWITH_LAME=OFF,lame"
PACKAGECONFIG[faad] = "-DWITH_FAAD=ON,-DWITH_FAAD=OFF,faad2"
PACKAGECONFIG[faac] = "-DWITH_FAAC=ON,-DWITH_FAAC=OFF,faac"
PACKAGECONFIG[test] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF"

do_configure:prepend() {
    sed -i 's|TEST_SOURCE_DIR="\${CMAKE_CURRENT_SOURCE_DIR}"|TEST_SOURCE_DIR="${PTEST_PATH}/test"|' ${S}/client/common/test/CMakeLists.txt
    sed -i 's|\${CMAKE_SOURCE_DIR}|${PTEST_PATH}|' ${S}/winpr/libwinpr/clipboard/test/CMakeLists.txt
    sed -i 's|="\${CMAKE_CURRENT_SOURCE_DIR}|="${PTEST_PATH}/test|' ${S}/winpr/libwinpr/utils/test/CMakeLists.txt
    sed -i 's|="\${CMAKE_CURRENT_BINARY_DIR}|="${PTEST_PATH}/test|' ${S}/winpr/libwinpr/utils/test/CMakeLists.txt
    sed -i 's|="\${CMAKE_CURRENT_SOURCE_DIR}|="${PTEST_PATH}/test|' ${S}/libfreerdp/codec/test/CMakeLists.txt
    sed -i 's|="\${CMAKE_CURRENT_BINARY_DIR}|="${PTEST_PATH}/test|' ${S}/libfreerdp/codec/test/CMakeLists.txt

}

do_configure:append() {
    sed -i -e 's|${WORKDIR}||g' ${B}/include/freerdp/buildflags.h
    sed -i -e 's|${WORKDIR}||g' ${B}/winpr/include/winpr/buildflags.h
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    # main CTestTestfile.cmake file
    cp ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    # the actual test executables
    cp -r ${B}/Testing ${D}${PTEST_PATH}
    # test data
    cp ${S}/winpr/libwinpr/utils/test/*bmp ${D}${PTEST_PATH}/test/
    cp ${S}/libfreerdp/codec/test/*bmp ${D}${PTEST_PATH}/test/
    cp -r ${S}/client/common/test/* ${D}${PTEST_PATH}/test/
    cp -r ${S}/resources ${D}${PTEST_PATH}
    cp -r ${S}/libfreerdp/codec/test/planar ${D}${PTEST_PATH}/test/
    cp -r ${S}/libfreerdp/codec/test/interleaved ${D}${PTEST_PATH}/test/

    cd ${B}
    # the test definitions, how to execute the tests
    find . -name CTestTestfile.cmake -exec install -Dm 0644 {} ${D}${PTEST_PATH}/{} \;

    for testfile in $(find ${D}${PTEST_PATH} -name CTestTestfile.cmake); do
        # these are comments only, containing ${S} and ${B}, at the top of each file
        sed -i "s,Source directory: ${S},<source_dir>," $testfile
        sed -i "s,Build directory: ${B},<build_dir>," $testfile

        # change the ${B} to ${PTEST_PATH}, so the files will be searches at the correct place
        sed -i "s,${B}/Testing,${PTEST_PATH}/Testing,g" $testfile

        # These add some extra traceability info to the tests, to pair them with CMakeLists.txt files,
        # containing ${B}.
        # They are not needed for test execution, just remove the whole line.
        sed -i "s,set_tests_properties.*_BACKTRACE_TRIPLES.*,," $testfile
    done

    # This is not part of the tests it is just in the same folder, and requires Python to be installed.
    # Just remove it.
    rm ${D}${PTEST_PATH}/resources/conv_to_ewm_prop.py

    # This particular test requires openh264, which is part of meta-multimedia layer.
    # Since it is not a dependency of meta-oe, comment this test out.
    sed -i 's/add_test(TestFreeRDPCodecH264/#add_test(TestFreeRDPCodecH264/' ${D}${PTEST_PATH}/libfreerdp/codec/test/CTestTestfile.cmake
}

PACKAGES =+ "${PN}-proxy-plugins"

FILES:${PN}-proxy-plugins += "${libdir}/${BPN}/proxy/*.so*"

FILES:${PN} += "${datadir}"

SYSROOT_DIRS += "${bindir}"

INSANE_SKIP:${PN}-proxy-plugins  += "dev-so"
