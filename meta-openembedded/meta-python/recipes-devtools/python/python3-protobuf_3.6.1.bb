inherit setuptools3
require python-protobuf.inc

SRC_URI += "file://0001-Add-Python-3.7-compatibility-4862.patch"
DEPENDS += "protobuf"
DISTUTILS_BUILD_ARGS += "--cpp_implementation"
DISTUTILS_INSTALL_ARGS += "--cpp_implementation"

do_compile_prepend_class-native () {
    export KOKORO_BUILD_NUMBER="1"
}
