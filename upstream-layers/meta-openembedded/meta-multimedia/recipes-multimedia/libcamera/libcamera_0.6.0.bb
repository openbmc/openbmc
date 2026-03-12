SUMMARY = "Linux libcamera framework"
HOMEPAGE = "https://libcamera.org/"
SECTION = "libs"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "\
    file://LICENSES/GPL-2.0-or-later.txt;md5=fed54355545ffd980b814dab4a3b312c \
    file://LICENSES/LGPL-2.1-or-later.txt;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
"

SRC_URI = " \
        git://git.libcamera.org/libcamera/libcamera.git;protocol=https;branch=master;tag=v${PV} \
        file://0001-libcamera-Do-not-assume-libc-with-clang.patch \
"

SRCREV = "3c17d1fbb2bd93f221afee788ebf0d7394032e4d"

PE = "1"

DEPENDS = "python3-pyyaml-native python3-jinja2-native python3-ply-native python3-jinja2-native udev gnutls chrpath-native libevent libyaml"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'qt', 'qtbase qtbase-native', '', d)}"

PACKAGES =+ "${PN}-gst ${PN}-pycamera"

PACKAGECONFIG ??= ""
PACKAGECONFIG[dng] = ",,tiff"
PACKAGECONFIG[gst] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[pycamera] = "-Dpycamera=enabled,-Dpycamera=disabled,python3 python3-pybind11"
PACKAGECONFIG[raspberrypi] = ",,libpisp"

# Raspberry Pi requires the meta-raspberrypi layer
# These values are coming from the project's meson.build file,
# which lists the supported values by arch.
ARM_PIPELINES = "${@bb.utils.contains('PACKAGECONFIG', 'raspberrypi', 'rpi/pisp,rpi/vc4,', '', d)}"
ARM_PIPELINES .= "imx8-isi,mali-c55,simple,uvcvideo"

LIBCAMERA_PIPELINES ??= "auto"
LIBCAMERA_PIPELINES:arm ??= "${ARM_PIPELINES}"
LIBCAMERA_PIPELINES:aarch64 ??= "${ARM_PIPELINES}"

EXTRA_OEMESON = " \
    -Dpipelines=${LIBCAMERA_PIPELINES} \
    -Dv4l2=true \
    -Dcam=enabled \
    -Dlc-compliance=disabled \
    -Dtest=false \
    -Ddocumentation=disabled \
"

RDEPENDS:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland qt', 'qtwayland', '', d)}"

inherit meson pkgconfig python3native

do_configure:prepend() {
    sed -i -e 's|py_compile=True,||' ${S}/utils/codegen/ipc/mojo/public/tools/mojom/mojom/generate/template_expander.py
}

do_install:append() {
    chrpath -d ${D}${libdir}/libcamera.so
    chrpath -d ${D}${libexecdir}/libcamera/v4l2-compat.so
}

do_package:append() {
    bb.build.exec_func("do_package_recalculate_ipa_signatures", d)
}

do_package_recalculate_ipa_signatures() {
    local modules
    for module in $(find ${PKGD}/usr/lib/libcamera -name "*.so.sign"); do
        module="${module%.sign}"
        if [ -f "${module}" ] ; then
            modules="${modules} ${module}"
        fi
    done

    ${S}/src/ipa/ipa-sign-install.sh ${B}/src/ipa-priv-key.pem "${modules}"
}

FILES:${PN} += " ${libexecdir}/libcamera/v4l2-compat.so"
FILES:${PN}-gst = "${libdir}/gstreamer-1.0"
FILES:${PN}-pycamera = "${PYTHON_SITEPACKAGES_DIR}/libcamera"

# libcamera-v4l2 explicitly sets _FILE_OFFSET_BITS=32 to get access to
# both 32 and 64 bit file APIs.
GLIBC_64BIT_TIME_FLAGS = ""

INSANE_SKIP += "32bit-time"
